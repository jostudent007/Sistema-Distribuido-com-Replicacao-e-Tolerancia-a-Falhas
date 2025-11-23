package com.projeto1.communication.strategy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.projeto1.communication.RequestHandler;
import com.sun.net.httpserver.HttpServer;

public class HttpCommunicationStrategy implements CommunicationStrategy {

    @Override
    public void startServer(int port, RequestHandler handler) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            // Cria um contexto para receber todas as requisições no path raiz "/".
            server.createContext("/", httpExchange -> {
                try (httpExchange) {
                    String command = "";

                    // Verifica qual o método da requisição: GET ou POST.
                    if ("GET".equals(httpExchange.getRequestMethod())) {
                        // Se for GET, pega o caminho do URI. Ex: http://localhost:8080/status -> "/status"
                        String path = httpExchange.getRequestURI().getPath();
                        // Se o caminho for /status, define o comando como "status".
                        if ("/status".equalsIgnoreCase(path)) {
                            command = "status";
                        }
                    } else if ("POST".equals(httpExchange.getRequestMethod())) {
                        // Se for POST, lê o corpo da requisição.
                        InputStream is = httpExchange.getRequestBody();
                        command = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    }
                    if (command.isEmpty()) {
                        // Se não for um GET para /status nem um POST, retorna um erro.
                        String errorMessage = "ERROR: Use POST method with command in body, or GET /status.";
                        httpExchange.sendResponseHeaders(400, errorMessage.getBytes().length); // 400 = Bad Request
                        OutputStream os = httpExchange.getResponseBody();
                        os.write(errorMessage.getBytes());
                        return;
                    }
                    // Passa o comando extraído (seja do GET ou do POST) para o handler principal.
                    String responseBody = handler.handle(command);
                    byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);

                    // Determina o tipo de conteúdo da resposta.
                    String contentType = responseBody.trim().toLowerCase().startsWith("<html>") ? "text/html" : "text/plain";
                    httpExchange.getResponseHeaders().set("Content-Type", contentType);

                    // Envia a resposta de volta para o cliente.
                    httpExchange.sendResponseHeaders(200, responseBytes.length);
                    OutputStream os = httpExchange.getResponseBody();
                    os.write(responseBytes);

                } catch (Exception e) {
                    System.err.println("Erro ao tratar requisição HTTP: " + e.getMessage());
                }
            });

            server.setExecutor(null);
            server.start();
            System.out.println("Servidor HTTP (aprimorado) iniciado na porta " + port);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao iniciar o servidor HTTP", e);
        }
    }

    @Override
    public String sendMessage(String host, int port, String message) throws IOException {
        // Cria uma instância do cliente HTTP moderno do Java.
        HttpClient client = HttpClient.newHttpClient();

        // Constrói a requisição HTTP.
        HttpRequest request = HttpRequest.newBuilder()
                // Define o URI (endereço) do servidor.
                .uri(URI.create("http://" + host + ":" + port + "/"))
                // Define o método como POST e fornece o corpo da requisição.
                .POST(HttpRequest.BodyPublishers.ofString(message, StandardCharsets.UTF_8))
                // Adiciona um cabeçalho para indicar o tipo de conteúdo.
                .header("Content-Type", "text/plain; charset=UTF-8")
                .build();

        try {
            // Envia a requisição e especifica que o corpo da resposta deve ser tratado como uma String.
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Retorna o corpo da resposta.
            return response.body();
        } catch (InterruptedException e) {
            // Se a thread for interrompida enquanto espera, restaura o status de interrupção.
            Thread.currentThread().interrupt();
            throw new IOException("Envio da requisição HTTP foi interrompido", e);
        }
    }
}