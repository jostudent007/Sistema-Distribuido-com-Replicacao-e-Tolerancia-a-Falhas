package com.projeto1.communication;

@FunctionalInterface
public interface RequestHandler {
    String handle(String requestData);
}