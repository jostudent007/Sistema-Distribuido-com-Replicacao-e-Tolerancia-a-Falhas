package com.projeto1.communication.strategy;

import com.projeto1.communication.RequestHandler;
import com.projeto1.grpc.CommunicationServiceGrpc;
import com.projeto1.grpc.RequestMessage;
import com.projeto1.grpc.ResponseMessage;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class GrpcCommunicationStrategy implements CommunicationStrategy {

    // ================== MUDANÇA PRINCIPAL: CACHE DE CANAIS ==================
    // Usamos um ConcurrentHashMap para armazenar os canais de forma thread-safe.
    // A chave será o endereço do servidor (ex: "localhost:9001").
    // Isso evita a criação e destruição de canais a cada requisição.
    private final ConcurrentHashMap<String, ManagedChannel> channels = new ConcurrentHashMap<>();
    // =========================================================================

    @Override
    public void startServer(int port, RequestHandler handler) {
        // Inicia o servidor em uma nova thread para não bloquear a aplicação.
        new Thread(() -> {
            try {
                CommunicationServiceImpl service = new CommunicationServiceImpl(handler);
                final Server server = ServerBuilder.forPort(port).addService(service).build();

                // Adiciona um shutdown hook para desligar o servidor E os canais de cliente.
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    System.err.println("*** Recebido sinal de desligamento, iniciando shutdown do gRPC... ***");
                    // Desliga todos os canais de cliente que foram criados e cacheados.
                    channels.values().forEach(ManagedChannel::shutdown);
                    // Desliga o servidor.
                    server.shutdown();
                    System.err.println("*** Shutdown do servidor e dos canais de cliente gRPC iniciado. ***");
                }));

                server.start();
                System.out.println("Servidor gRPC iniciado na porta " + port);
                server.awaitTermination();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Falha no servidor gRPC", e);
            }
        }).start();
    }

    /**
     * Obtém um canal de comunicação para um host/porta específico.
     * Se um canal para este endereço já existir no cache, ele é reutilizado.
     * Caso contrário, um novo canal é criado, armazenado no cache e retornado.
     */
    private ManagedChannel getChannel(String host, int port) {
        String address = host + ":" + port;
        // O método computeIfAbsent garante que o canal seja criado apenas uma vez,
        // mesmo que múltiplas threads tentem acessá-lo ao mesmo tempo.
        return channels.computeIfAbsent(address, addr ->
                ManagedChannelBuilder.forAddress(host, port)
                        .usePlaintext()
                        .build()
        );
    }

    @Override
    public String sendMessage(String host, int port, String message) throws IOException {
        // 1. Obtém um canal REUTILIZÁVEL do nosso cache.
        ManagedChannel channel = getChannel(host, port);

        // 2. Cria um stub (cliente) para fazer a chamada.
        CommunicationServiceGrpc.CommunicationServiceBlockingStub stub = CommunicationServiceGrpc.newBlockingStub(channel);

        // 3. Constrói a mensagem de requisição.
        RequestMessage request = RequestMessage.newBuilder().setContent(message).build();

        // 4. Faz a chamada RPC remota.
        ResponseMessage response = stub.sendMessage(request);

        // 5. IMPORTANTE: NÃO fechamos mais o canal aqui.
        // Ele permanece aberto no cache para ser reutilizado pela próxima requisição.
        return response.getContent();
    }

    // A classe interna que implementa o serviço permanece a mesma.
    private static class CommunicationServiceImpl extends CommunicationServiceGrpc.CommunicationServiceImplBase {
        private final RequestHandler handler;

        public CommunicationServiceImpl(RequestHandler handler) {
            this.handler = handler;
        }

        @Override
        public void sendMessage(RequestMessage request, StreamObserver<ResponseMessage> responseObserver) {
            String responseContent = this.handler.handle(request.getContent());
            ResponseMessage response = ResponseMessage.newBuilder().setContent(responseContent).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}