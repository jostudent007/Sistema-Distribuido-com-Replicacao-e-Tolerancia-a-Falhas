package com.projeto1.communication.strategy;

import java.io.IOException;

import com.projeto1.communication.RequestHandler;

public interface CommunicationStrategy {
    void startServer(int port, RequestHandler handler);
    String sendMessage(String host, int port, String message) throws IOException;
}