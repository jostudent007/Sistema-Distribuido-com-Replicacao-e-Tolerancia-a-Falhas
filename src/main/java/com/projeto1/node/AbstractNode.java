package com.projeto1.node;

import com.projeto1.communication.CommunicationFactory;
import com.projeto1.communication.strategy.CommunicationStrategy;
import com.projeto1.node.state.KeyValueStore;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Classe base abstrata com a lógica comum para Líder e Seguidores.
 */
public abstract class AbstractNode {
    protected final KeyValueStore keyValueStore = new KeyValueStore();
    protected final String host;
    protected final int port;
    protected final String gatewayAddress;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // ================== MUDANÇA PRINCIPAL AQUI ==================
    // 1. NOVO CAMPO: Cada nó agora tem sua própria instância de cliente de comunicação.
    // Ela será criada uma vez no 'start' e reutilizada para todas as chamadas de registro.
    protected CommunicationStrategy clientStrategy;
    // ==========================================================

    public AbstractNode(String host, int port, String gatewayAddress) {
        this.host = host;
        this.port = port;
        this.gatewayAddress = gatewayAddress;
    }

    /**
     * Inicia o servidor do nó e o processo de registro periódico.
     */
    public void start(String protocol) {
        // Inicia o servidor para escutar requisições do Gateway (lógica do "ping").
        CommunicationStrategy serverStrategy = CommunicationFactory.getStrategy(protocol);
        serverStrategy.startServer(port, request -> {
            if ("ping".equalsIgnoreCase(request.trim())) {
                return "pong";
            }
            return handleRequest(request);
        });

        // 2. INICIALIZAÇÃO ÚNICA: A estratégia de cliente é criada apenas uma vez aqui.
        this.clientStrategy = CommunicationFactory.getStrategy(protocol);

        System.out.printf("[%s] Nó iniciado em %s:%d%n", getNodeType(), host, port);

        // 3. TAREFA AGENDADA: A tarefa agora chama o método registerSelf() sem argumentos.
        scheduler.scheduleAtFixedRate(this::registerSelf, 0, 10, TimeUnit.SECONDS);
    }

    /**
     * Envia uma mensagem de registro para o API Gateway usando a estratégia de cliente persistente.
     */
    private void registerSelf() {
        try {
            String[] gatewayParts = gatewayAddress.split(":");
            String gatewayHost = gatewayParts[0];
            int gatewayPort = Integer.parseInt(gatewayParts[1]);

            String registerCommand = String.format("register %s %s %d", getNodeType(), host, port);
            System.out.printf("[%s] Enviando registro para o Gateway: %s%n", getNodeType(), registerCommand);

            // 4. USO DA INSTÂNCIA PERSISTENTE: Usa o 'clientStrategy' do objeto, em vez de criar um novo.
            // Isso garante que o cache de canais gRPC seja reutilizado e não haja vazamentos.
            clientStrategy.sendMessage(gatewayHost, gatewayPort, registerCommand);

        } catch (Exception e) { // Captura Exception para ser robusto a todos os protocolos.
            System.err.printf("[%s] Falha ao se registrar no Gateway: %s%n", getNodeType(), e.getMessage());
        }
    }

    // Métodos abstratos implementados por LeaderNode e FollowerNode
    protected abstract String handleRequest(String request);
    protected abstract String getNodeType();
}