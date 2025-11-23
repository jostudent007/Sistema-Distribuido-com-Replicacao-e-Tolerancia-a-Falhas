package com.projeto1.gateway;

import com.projeto1.common.NodeInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Gerencia o registro de todos os nós, agrupando-os por tipo (LEADER, FOLLOWER, HTML, etc.).
 * Implementado como um Singleton para garantir uma única instância e fonte da verdade.
 */
public class ServiceRegistry {
    // Instância única do Singleton.
    private static final ServiceRegistry INSTANCE = new ServiceRegistry();

    // Estrutura principal: Mapa que armazena listas de nós, agrupados por seu tipo (nodeType).
    // Ex: "FOLLOWER" -> [nó1, nó2], "HTML" -> [nó3, nó4]
    // A lista é um CopyOnWriteArrayList, que é seguro para leituras concorrentes (muito comum em gateways).
    private final ConcurrentHashMap<String, List<NodeInfo>> nodesByType = new ConcurrentHashMap<>();

    // Mapa auxiliar para encontrar e remover nós rapidamente por seu ID único.
    private final ConcurrentHashMap<String, NodeInfo> nodesById = new ConcurrentHashMap<>();

    // Mapa de contadores para o balanceamento de carga round-robin de cada tipo de componente.
    private final Map<String, AtomicInteger> roundRobinCounters = new ConcurrentHashMap<>();

    // Construtor privado.
    private ServiceRegistry() {}

    /**
     * Retorna a instância única do ServiceRegistry.
     */
    public static ServiceRegistry getInstance() {
        return INSTANCE;
    }
    /**
     * Registra um novo nó no sistema.
     * @param nodeInfo As informações do nó a ser registrado.
     */
    public void registerNode(NodeInfo nodeInfo) {
        // Adiciona ou atualiza o nó no mapa por ID para facilitar o heartbeat.
        nodesById.put(nodeInfo.nodeId(), nodeInfo);

        // Garante que a lista para este tipo de nó exista. Se não existir, cria uma nova.
        nodesByType.computeIfAbsent(nodeInfo.nodeType(), k -> new CopyOnWriteArrayList<>());

        // Remove qualquer registro antigo deste mesmo nó (baseado no ID) para evitar duplicatas na lista.
        nodesByType.get(nodeInfo.nodeType()).removeIf(node -> node.nodeId().equals(nodeInfo.nodeId()));

        // Adiciona a informação mais recente do nó à lista de seu tipo.
        nodesByType.get(nodeInfo.nodeType()).add(nodeInfo);

        // Garante que o contador para o round-robin exista para este tipo de nó.
        roundRobinCounters.computeIfAbsent(nodeInfo.nodeType(), k -> new AtomicInteger(0));

        System.out.println("[ServiceRegistry] Nó registrado/atualizado: " + nodeInfo);
    }

    /**
     * Remove um nó que falhou de todos os registros.
     * @param nodeId O ID do nó a ser removido.
     */
    public void deregisterNode(String nodeId) {
        NodeInfo removedNode = nodesById.remove(nodeId);
        if (removedNode != null) {
            // Remove o nó também da lista específica de seu tipo.
            List<NodeInfo> nodeList = nodesByType.get(removedNode.nodeType());
            if (nodeList != null) {
                nodeList.remove(removedNode);
            }
            System.out.println("[ServiceRegistry] Nó removido por falha: " + removedNode);
        }
    }

    /**
     * Retorna o próximo nó de um tipo específico, usando balanceamento de carga round-robin.
     * @param nodeType O tipo do componente (ex: "FOLLOWER" ou "HTML").
     * @return O NodeInfo do próximo nó, ou null se não houver nós daquele tipo.
     */
    public NodeInfo getNextNode(String nodeType) {
        List<NodeInfo> nodeList = nodesByType.getOrDefault(nodeType, Collections.emptyList());
        if (nodeList.isEmpty()) {
            return null;
        }
        // Lógica de Round-Robin: incrementa o contador e usa o resto da divisão
        // pelo tamanho da lista para obter um índice circular.
        int index = roundRobinCounters.get(nodeType).getAndIncrement() % nodeList.size();
        return nodeList.get(index);
    }
    /**
     * Retorna o nó Líder. Como só deve haver um, pega o primeiro da lista.
     * @return O NodeInfo do Líder, ou null se não houver.
     */
    public NodeInfo getLeader() {
        return getNextNode("LEADER"); // Reutiliza a mesma lógica, pegando o primeiro (e único) da lista.
    }
    /**
     * Retorna uma lista de todos os nós Seguidores ativos.
     * @return Uma lista de NodeInfo dos Seguidores.
     */
    public List<NodeInfo> getFollowers() {
        return nodesByType.getOrDefault("FOLLOWER", Collections.emptyList());
    }
    /**
     * Retorna a lista completa de todos os nós ativos, de todos os tipos.
     * @return Uma lista com todos os NodeInfo registrados.
     */
    public List<NodeInfo> getAllNodes() {
        return List.copyOf(nodesById.values());
    }
}