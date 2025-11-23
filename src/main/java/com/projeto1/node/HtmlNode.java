package com.projeto1.node;

/**
 * Representa o Componente B: um serviço de status/homepage STATELESS.
 * Sua única função é retornar uma string HTML simples.
 * Como é stateless, podemos ter várias instâncias dele para balanceamento de carga.
 */
public class HtmlNode extends AbstractNode {

    public HtmlNode(String host, int port, String gatewayAddress) {
        super(host, port, gatewayAddress);
    }

    @Override
    protected String handleRequest(String request) {
        System.out.printf("[%s] Requisição recebida: %s%n", getNodeType(), request);
        String command = request.split(" ")[0].toLowerCase();

        // A única responsabilidade deste componente é responder ao comando 'status'.
        if ("status".equals(command)) {
            // Retorna uma string que é um HTML bruto.
            return "<html><body><h1>Sistema de Banco de Dados Distribuido</h1><p>Status: Operacional</p></body></html>";
        }
        return "ERROR: HtmlNode only accepts 'status' command.";
    }
    @Override
    protected String getNodeType() {
        // Usamos um novo tipo para que o Gateway possa identificá-lo.
        return "HTML";
    }
    // Como este nó é stateless, o keyValueStore da classe pai não é utilizado.
}