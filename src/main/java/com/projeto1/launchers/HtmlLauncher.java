package com.projeto1.launchers;

import com.projeto1.node.HtmlNode;

/**
 * Classe principal para iniciar uma instância do Nó HTML.
 */
public class HtmlLauncher {
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Uso: HtmlLauncher <host> <porta> <protocolo> <gateway_address>");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String protocol = args[2];
        String gatewayAddress = args[3];

        new HtmlNode(host, port, gatewayAddress).start(protocol);
    }
}