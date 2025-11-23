package com.projeto1.node;

/**
 * Representa o Nó Líder (Componente A).
 * Seu papel principal é processar todas as operações de escrita (`set`)
 * e também pode servir leituras (`get`) como fallback.
 */
public class LeaderNode extends AbstractNode {
    public LeaderNode(String host, int port, String gatewayAddress) {
        super(host, port, gatewayAddress);
    }

    @Override
    protected String handleRequest(String request) {
        System.out.printf("[%s] Requisição recebida: %s%n", getNodeType(), request);
        String[] parts = request.split(" ", 3);
        String command = parts[0].toLowerCase();

        // Se o comando é 'set' e tem 3 partes (set key value)
        if ("set".equals(command) && parts.length == 3) {
            // Armazena o dado no seu KeyValueStore local.
            keyValueStore.set(parts[1], parts[2]);
            // Retorna "OK" para o Gateway, sinalizando que a escrita foi bem-sucedida.
            return "OK";
        }
        // Se o comando é 'get' (caso de fallback)
        else if ("get".equals(command) && parts.length == 2) {
            return keyValueStore.get(parts[1]);
        }
        return "ERROR: Leader accepts 'set <key> <value>' or 'get <key>'.";
    }

    @Override
    protected String getNodeType() {
        return "LEADER";
    }
}
