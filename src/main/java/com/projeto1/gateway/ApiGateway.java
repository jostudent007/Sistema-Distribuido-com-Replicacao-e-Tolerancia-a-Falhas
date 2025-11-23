package com.projeto1.gateway;

import com.projeto1.common.NodeInfo;
import com.projeto1.communication.CommunicationFactory;
import com.projeto1.communication.strategy.CommunicationStrategy;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * O API Gateway é o ponto de entrada único para o sistema distribuído.
 * Ele é responsável por:
 * 1. Receber requisições dos clientes.
 * 2. Manter um registro de nós ativos (Service Discovery).
 * 3. Rotear as requisições para o nó apropriado (Líder ou Seguidor).
 */
public class ApiGateway {

    private final ServiceRegistry serviceRegistry = ServiceRegistry.getInstance();
    private final AtomicInteger followerCounter = new AtomicInteger(0);

    public void start(int port, String protocol) {
        // Obtém a estratégia de comunicação (UDP, HTTP, gRPC) através da Factory.
        CommunicationStrategy communicationStrategy = CommunicationFactory.getStrategy(protocol);

        // Inicia o servidor do Gateway e define o que fazer com as mensagens recebidas.
        // A lógica de tratamento de requisições é passada como uma expressão lambda.
        communicationStrategy.startServer(port, request -> {
            System.out.println("[ApiGateway] Requisição recebida: " + request);
            try {
                // Delega o processamento da requisição para o método handleRequest.
                return handleRequest(request, communicationStrategy);
            } catch (IOException e) {
                System.err.println("[ApiGateway] Erro ao rotear requisição: " + e.getMessage());
                return "ERROR: Roteamento falhou.";
            }
        });

        System.out.printf("[ApiGateway] iniciado na porta %d usando protocolo %s%n", port, protocol.toUpperCase());

        // ================== CORREÇÃO NA INICIALIZAÇÃO DO HEARTBEAT ==================
        // Agora passamos o 'serviceRegistry' para o construtor do monitor,
        // garantindo que ele tenha a referência correta para remover os nós.
        HeartbeatMonitor heartbeatMonitor = new HeartbeatMonitor(this.serviceRegistry, protocol);
        new Thread(heartbeatMonitor).start();
        System.out.println("[ApiGateway] Monitor de Heartbeat iniciado.");
        // ================== FIM DA CORREÇÃO ==================
    }

    /**
     * Processa e roteia a requisição recebida para o destino correto.
     * Este método é o coração da lógica de roteamento e da implementação do padrão Follower Reads.
     *
     * @param request A mensagem de requisição recebida como uma String ("set mykey 123").
     * @param strategy A estratégia de comunicação a ser usada para encaminhar a mensagem.
     * @return A resposta final a ser enviada de volta ao cliente.
     * @throws IOException Se ocorrer um erro de comunicação ao encaminhar a requisição.
     */
    private String handleRequest(String request, CommunicationStrategy strategy) throws IOException {
        // Divide a string da requisição em partes usando o espaço como delimitador.
        // Ex: "set mykey 123" se torna um array ["set", "mykey", "123"].
        String[] parts = request.split(" ");
        // Pega a primeira parte, que é o comando, e a converte para minúsculo para facilitar a comparação.
        String command = parts[0].toLowerCase();

        // Um switch-case para executar lógicas diferentes dependendo do comando recebido.
        switch (command) {
            // --- Caso: Um nó está se registrando no Gateway ---
            case "register":
                String nodeType = parts[1].toUpperCase(); // Ex: "LEADER", "FOLLOWER", "HTML"
                String host = parts[2];                   // Ex: "localhost"
                int nodePort = Integer.parseInt(parts[3]); // Ex: 9001
                String nodeId = nodeType + "-" + host + ":" + nodePort; // Cria um ID único.
                // Chama o ServiceRegistry para adicionar ou atualizar as informações do nó.
                serviceRegistry.registerNode(new NodeInfo(nodeId, host, nodePort, nodeType));
                // Retorna uma mensagem de confirmação para o nó que se registrou.
                return "ACK: Node " + nodeId + " registered.";

            // --- Caso: Uma requisição de escrita (SET) ---
            case "set":
                // Pede ao ServiceRegistry para encontrar o nó Líder.
                NodeInfo leader = serviceRegistry.getLeader();
                if (leader == null) return "ERROR: No leader available.";

                // Encaminha a requisição original ("set mykey 123") para o Líder.
                String leaderResponse = strategy.sendMessage(leader.host(), leader.port(), request);

                // Se o Líder respondeu com "OK", significa que a escrita foi bem-sucedida.
                if ("OK".equals(leaderResponse)) {
                    // Prepara um comando de replicação para os seguidores.
                    String key = parts[1];
                    String value = parts[2];
                    String replicationRequest = "replicate " + key + " " + value;

                    System.out.println("[ApiGateway] Líder confirmou a escrita. Replicando para seguidores...");
                    // Itera sobre a lista de todos os seguidores ativos.
                    for (NodeInfo followerNode : serviceRegistry.getFollowers()) {
                        try {
                            // Envia o comando de replicação para cada seguidor.
                            strategy.sendMessage(followerNode.host(), followerNode.port(), replicationRequest);
                        } catch (IOException e) {
                            System.err.println("[ApiGateway] Falha ao replicar para o seguidor " + followerNode.nodeId() + ": " + e.getMessage());
                        }
                    }
                }
                return leaderResponse;

            // --- Caso: Uma requisição de leitura (GET) ---
            case "get":
                // Pede ao ServiceRegistry a lista de todos os seguidores disponíveis.
                List<NodeInfo> followers = serviceRegistry.getFollowers();
                if (followers.isEmpty()) {
                    // Tenta usar o Líder para a leitura se não houver seguidores.
                    NodeInfo leaderAsReader = serviceRegistry.getLeader();
                    if (leaderAsReader == null) return "ERROR: No nodes available for reading.";

                    System.out.println("[ApiGateway] Nenhum seguidor disponível. Roteando GET para o líder: " + leaderAsReader.nodeId());
                    return strategy.sendMessage(leaderAsReader.host(), leaderAsReader.port(), request);
                }
                // Implementa o balanceamento de carga Round-Robin (rodízio).
                // Pega o valor atual do contador, incrementa e usa o resto da divisão para obter um índice circular.
                int followerIndex = followerCounter.getAndIncrement() % followers.size();
                NodeInfo follower = followers.get(followerIndex);

                System.out.println("[ApiGateway] Roteando GET para o seguidor: " + follower.nodeId());
                // Encaminha a requisição de leitura para o seguidor escolhido.
                return strategy.sendMessage(follower.host(), follower.port(), request);

            // --- Caso: Uma requisição de status ---
            case "status":
                // Pede ao ServiceRegistry o próximo nó do tipo "HTML" (com balanceamento de carga).
                NodeInfo htmlNode = serviceRegistry.getNextNode("HTML");
                if (htmlNode != null) {
                    System.out.println("[ApiGateway] Roteando comando 'status' para o nó HTML: " + htmlNode.nodeId());
                    // Encaminha a requisição para o nó de HTML.
                    return strategy.sendMessage(htmlNode.host(), htmlNode.port(), request);
                } else {
                    return "ERROR: No HTML service node is available.";
                }
            default:
                return "ERROR: Unknown command '" + command + "'.";
        }
    }
}