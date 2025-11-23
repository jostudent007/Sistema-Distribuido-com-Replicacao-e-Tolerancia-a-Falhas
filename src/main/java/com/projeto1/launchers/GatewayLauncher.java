package com.projeto1.launchers;

import com.projeto1.gateway.ApiGateway;

/**
 * Classe principal para iniciar uma instância do Gateway.
 */
public class GatewayLauncher {
    public static void main(String[] args) {
        // Argumentos esperados: <porta> <protocolo>
        // Exemplo: 8080 http
        if (args.length < 2) {
            System.out.println("Uso: GatewayLauncher <porta> <protocolo>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        String protocol = args[1];

        ApiGateway gateway = new ApiGateway();
        gateway.start(port, protocol);
    }
}