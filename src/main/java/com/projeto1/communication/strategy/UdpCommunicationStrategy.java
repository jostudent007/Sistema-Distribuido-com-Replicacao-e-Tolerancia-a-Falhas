package com.projeto1.communication.strategy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

import com.projeto1.communication.RequestHandler;

public class UdpCommunicationStrategy implements CommunicationStrategy {

    private static final int BUFFER_SIZE = 4096; // Aumentado para pacotes maiores

    @Override
    public void startServer(int port, RequestHandler handler) {
        // O servidor UDP precisa rodar em uma thread separada para não bloquear a execução principal.
        new Thread(() -> {
            // try-with-resources garante que o socket será fechado automaticamente.
            try (DatagramSocket socket = new DatagramSocket(port)) {
                System.out.println("Servidor UDP iniciado na porta " + port);
                byte[] buffer = new byte[BUFFER_SIZE];

                // Loop infinito para continuamente escutar por novos pacotes.
                while (true) {
                    // Cria um pacote para receber os dados.
                    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                    // Bloqueia a execução até que um pacote seja recebido.
                    socket.receive(receivePacket);

                    // Converte os bytes recebidos em uma String. Usa o tamanho real do pacote.
                    String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);

                    // Processa a mensagem com o handler.
                    String responseMessage = handler.handle(receivedMessage);
                    byte[] responseBytes = responseMessage.getBytes(StandardCharsets.UTF_8);

                    // Obtém o endereço IP e a porta do cliente que enviou o pacote.
                    InetAddress clientAddress = receivePacket.getAddress();
                    int clientPort = receivePacket.getPort();

                    // Cria um novo pacote com a resposta e o envia de volta ao cliente.
                    DatagramPacket sendPacket = new DatagramPacket(responseBytes, responseBytes.length, clientAddress, clientPort);
                    socket.send(sendPacket);
                }
            } catch (IOException e) {
                System.err.println("Erro crítico no servidor UDP: " + e.getMessage());
            }
        }).start();
    }

    @Override
    public String sendMessage(String host, int port, String message) throws IOException {
        // try-with-resources para o socket do cliente.
        try (DatagramSocket socket = new DatagramSocket()) {
            // Converte o nome do host para um objeto InetAddress.
            InetAddress serverAddress = InetAddress.getByName(host);
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

            // Cria e envia o pacote com a mensagem para o servidor.
            DatagramPacket sendPacket = new DatagramPacket(messageBytes, messageBytes.length, serverAddress, port);
            socket.send(sendPacket);

            // Prepara um buffer para receber a resposta do servidor.
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

            // Define um timeout de 5 segundos. Se não houver resposta nesse tempo, uma exceção será lançada.
            // Isso é CRUCIAL para evitar que o cliente fique preso para sempre esperando por uma resposta.
            socket.setSoTimeout(5000);

            try {
                // Aguarda o recebimento do pacote de resposta.
                socket.receive(receivePacket);
                // Converte a resposta em String e a retorna.
                return new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
            } catch (SocketTimeoutException e) {
                // Trata o caso de timeout, informando que o servidor não respondeu.
                throw new IOException("Timeout: O servidor não respondeu a tempo.", e);
            }
        }
    }
}