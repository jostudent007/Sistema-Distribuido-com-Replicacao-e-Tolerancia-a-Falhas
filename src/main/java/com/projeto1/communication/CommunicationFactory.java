package com.projeto1.communication;

import com.projeto1.communication.strategy.CommunicationStrategy;
import com.projeto1.communication.strategy.GrpcCommunicationStrategy;
import com.projeto1.communication.strategy.HttpCommunicationStrategy;
import com.projeto1.communication.strategy.UdpCommunicationStrategy;

/**
 * Cria e fornece a instância da estratégia de comunicação correta, simplificando o uso do padrão Strategy
 */
public class CommunicationFactory {
    public static CommunicationStrategy getStrategy(String protocolType) {
        if (protocolType == null || protocolType.isEmpty()) {
            throw new IllegalArgumentException("Tipo de protocolo não pode ser nulo ou vazio.");
        }
        switch (protocolType.toLowerCase()) {
            case "http":
                return new HttpCommunicationStrategy();
            case "udp":
                return new UdpCommunicationStrategy();
            case "grpc":
                return new GrpcCommunicationStrategy();
            default:
                throw new IllegalArgumentException("Protocolo desconhecido: " + protocolType);
        }
    }
}
