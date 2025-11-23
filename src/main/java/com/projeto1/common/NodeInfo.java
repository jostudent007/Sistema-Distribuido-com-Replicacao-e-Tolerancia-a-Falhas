package com.projeto1.common;

/**
 * Um Record para armazenar informações sobre um nó registrado no sistema.
 * @param nodeId      Identificador único do nó (ex: "Leader-1", "Follower-1").
 * @param host        O endereço IP ou hostname do nó.
 * @param port        A porta em que o nó está escutando.
 * @param nodeType    O tipo do nó ("LEADER" ou "FOLLOWER").
 */
public record NodeInfo(String nodeId, String host, int port, String nodeType) {
    /**
     * Retorna o endereço completo do nó no formato "host:port".
     */
    public String getAddress() {
        return host + ":" + port;
    }
}