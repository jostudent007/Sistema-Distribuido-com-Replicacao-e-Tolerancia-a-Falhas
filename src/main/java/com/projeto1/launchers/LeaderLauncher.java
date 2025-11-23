package com.projeto1.launchers;

import com.projeto1.node.LeaderNode;

/**
 * Classe principal para iniciar uma instância do Nó Líder.
 */
public class LeaderLauncher {
    public static void main(String[] args) {
        // Argumentos esperados: <meu_host> <minha_porta> <protocolo> <gateway_host>:<gateway_porta>
        // Ex: localhost 9001 http localhost:8080
        if (args.length < 4) {
            System.out.println("Uso: LeaderLauncher <host> <porta> <protocolo> <gateway_address>");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String protocol = args[2];
        String gatewayAddress = args[3];

        // Cria e inicia o nó líder.
        new LeaderNode(host, port, gatewayAddress).start(protocol);
    }
}