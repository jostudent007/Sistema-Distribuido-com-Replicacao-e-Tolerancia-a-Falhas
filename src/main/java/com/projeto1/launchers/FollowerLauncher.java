package com.projeto1.launchers;

import com.projeto1.node.FollowerNode;

/**
 * Classe principal para iniciar uma instância de um Nó Seguidor.
 */
public class FollowerLauncher {
    public static void main(String[] args) {
        // Argumentos esperados: <meu_host> <minha_porta> <protocolo> <gateway_host>:<gateway_porta>
        // Ex: localhost 9002 http localhost:8080
        if (args.length < 4) {
            System.out.println("Uso: FollowerLauncher <host> <porta> <protocolo> <gateway_address>");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String protocol = args[2];
        String gatewayAddress = args[3];

        // Cria e inicia o nó seguidor.
        new FollowerNode(host, port, gatewayAddress).start(protocol);
    }
}