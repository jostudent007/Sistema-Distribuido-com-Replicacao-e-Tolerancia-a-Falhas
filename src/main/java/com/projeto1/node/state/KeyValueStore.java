package com.projeto1.node.state;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Encapsula o estado do nosso banco de dados chave-valor.
 * Usa um ConcurrentHashMap para garantir que as operações de leitura e escrita
 * sejam seguras em um ambiente com múltiplas threads (thread-safe).
 */
public class KeyValueStore {
    // O mapa que armazena os dados da nossa aplicação.
    private final ConcurrentHashMap<String, String> store = new ConcurrentHashMap<>();

    /**
     * Insere ou atualiza um valor associado a uma chave.
     */
    public void set(String key, String value) {
        System.out.println("[KeyValueStore] Armazenando: " + key + " = " + value);
        store.put(key, value);
    }
    /**
     * Recupera o valor associado a uma chave.
     * @return O valor, ou null se a chave não existir.
     */
    public String get(String key) {
        return store.get(key);
    }
    @Override
    public String toString() {
        return "KeyValueStore{" +
                "store=" + store +
                '}';
    }
}