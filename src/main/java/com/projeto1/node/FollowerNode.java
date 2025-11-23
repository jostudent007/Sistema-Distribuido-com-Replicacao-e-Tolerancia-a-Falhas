package com.projeto1.node;

/**
 * Representa um Nó Seguidor (Componente A).
 * Seu papel principal é processar operações de leitura (`get`) e
 * receber atualizações de estado do Gateway (comandos `replicate`).
 */
public class FollowerNode extends AbstractNode {
    public FollowerNode(String host, int port, String gatewayAddress) {
        super(host, port, gatewayAddress);
    }

    @Override
    protected String handleRequest(String request) {
        System.out.printf("[%s] Requisição recebida: %s%n", getNodeType(), request);
        String[] parts = request.split(" ", 3);
        String command = parts[0].toLowerCase();

        // Se o comando é 'get', retorna o valor do seu KeyValueStore.
        if ("get".equals(command) && parts.length == 2) {
            String value = keyValueStore.get(parts[1]);
            return value != null ? value : "NULL";
        }
        // Se o comando é 'replicate', atualiza seu KeyValueStore com o novo dado.
        else if ("replicate".equals(command) && parts.length == 3) {
            keyValueStore.set(parts[1], parts[2]);
            return "OK_REPLICATED";
        }
        return "ERROR: Follower accepts 'get <key>' or 'replicate <key> <value>'.";
    }

    @Override
    protected String getNodeType() {
        return "FOLLOWER";
    }
}