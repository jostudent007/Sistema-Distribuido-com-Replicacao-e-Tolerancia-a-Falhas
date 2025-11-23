package com.projeto1.gateway;

import com.projeto1.common.NodeInfo;
import com.projeto1.communication.CommunicationFactory;
import com.projeto1.communication.strategy.CommunicationStrategy;

/**
 * Esta classe é executada em uma thread separada e é responsável por
 * verificar periodicamente a saúde de todos os nós registrados.
 */
public class HeartbeatMonitor implements Runnable {

    private final ServiceRegistry serviceRegistry; // Agora recebe o registro
    private final CommunicationStrategy strategy;

    // O construtor agora recebe as dependências de que precisa.
    public HeartbeatMonitor(ServiceRegistry serviceRegistry, String protocol) {
        this.serviceRegistry = serviceRegistry;
        this.strategy = CommunicationFactory.getStrategy(protocol);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(5000);
                System.out.println("[HeartbeatMonitor] Executando verificação de saúde...");

                // Itera sobre uma cópia da lista de nós para evitar problemas de concorrência.
                for (NodeInfo node : serviceRegistry.getAllNodes()) {
                    try {
                        // Envia uma mensagem "ping" para o nó.
                        String response = strategy.sendMessage(node.host(), node.port(), "ping");
                        if (!"pong".equals(response)) {
                            System.out.println("[HeartbeatMonitor] Nó " + node.nodeId() + " respondeu de forma inesperada: " + response);
                        }
                    } catch (Exception e) {
                        // Se sendMessage lança uma exceção, o nó está offline.
                        System.err.println("[HeartbeatMonitor] Nó " + node.nodeId() + " não respondeu ao ping. Removendo.");
                        // Remove o nó do registro de serviços.
                        serviceRegistry.deregisterNode(node.nodeId());
                    }
                }

            } catch (InterruptedException e) {
                System.err.println("[HeartbeatMonitor] Thread de heartbeat interrompida.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}