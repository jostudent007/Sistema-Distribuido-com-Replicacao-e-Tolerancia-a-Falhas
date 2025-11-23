package com.projeto1.launchers;

import com.projeto1.communication.CommunicationFactory;
import com.projeto1.communication.strategy.CommunicationStrategy;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Classe principal para iniciar um Teste via terminal.
 */
public class ClientLauncher {
    public static void main(String[] args) {
        // Args: <gateway_host> <gateway_porta> <protocolo> <comando> [args_comando...]
        // Ex: localhost 8080 http set mykey 123
        if (args.length < 4) {
            System.out.println("Uso: ClientLauncher <gateway_host> <gateway_porta> <protocolo> <comando> [argumentos...]");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String protocol = args[2];
        // Junta o comando e seus argumentos em uma única string.
        String message = Arrays.stream(args, 3, args.length).collect(Collectors.joining(" "));

        try {
            CommunicationStrategy strategy = CommunicationFactory.getStrategy(protocol);

            System.out.println("Cliente enviando para Gateway: " + message);
            String response = strategy.sendMessage(host, port, message);
            System.out.println("Cliente recebeu do Gateway: " + response);

        } catch (IOException e) {
            System.err.println("Erro de comunicação do cliente: " + e.getMessage());
        }
    }
}