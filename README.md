# Projeto 1: Sistema Distribuído com Replicação e Tolerância a Falhas

Este projeto consiste no desenvolvimento de um sistema distribuído robusto em Java para a disciplina de Programação Distribuída, projetado para demonstrar padrões fundamentais de arquitetura distribuída, como replicação de dados, consistência, escalabilidade de leitura e resiliência a falhas.

O sistema é composto por múltiplos microsserviços que se comunicam via TCP, UDP ou gRPC para operações internas e expõem uma API HTTP para clientes externos através de um API Gateway.

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Arquitetura e Padrões](#-arquitetura-e-padrões)
- [Componentes](#-componentes)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Pré-requisitos](#-pré-requisitos)
- [Como Executar](#-como-executar)
- [Testes de Carga e Resiliência (JMeter)](#-testes-de-carga-e-resiliência-jmeter)
- [Autores](#-autor)

---

## 📖 Sobre o Projeto

O objetivo principal foi implementar uma arquitetura capaz de separar responsabilidades de escrita e leitura para otimizar o desempenho, mantendo a consistência dos dados através de replicação. Além disso, o sistema foi projetado para ser resiliente, detectando e recuperando-se automaticamente da falha de nós.

As principais funcionalidades incluem:
- Armazenamento de dados chave-valor em memória.
- Serviço de status HTML separado.
- Roteamento inteligente via API Gateway.
- Monitoramento ativo de saúde dos nós (Heartbeat).

---

## 🏗 Arquitetura e Padrões

O projeto implementa os seguintes padrões de arquitetura distribuída:

1.  **Leader and Followers (Líder e Seguidores):** Utilizado para o cluster de dados. Um único nó Líder recebe todas as escritas (`SET`) e as replica para múltiplos nós Seguidores, garantindo consistência.
2.  **Follower Reads (Leituras em Seguidores):** Para escalar a capacidade de leitura, as requisições `GET` são balanceadas entre os nós Seguidores, aliviando o Líder.
3.  **API Gateway:** Ponto único de entrada para clientes externos (HTTP), responsável pelo roteamento, Service Discovery e balanceamento de carga.
4.  **Service Discovery & Heartbeat:** Os nós se registram dinamicamente no Gateway. Um monitor de heartbeat verifica periodicamente a saúde dos nós, removendo automaticamente os que falharem.
---

## 🧩 Componentes

| Componente | Tipo | Responsabilidade | Porta Padrão | Protocolo Interno |
| :--- | :--- | :--- | :--- | :--- |
| **API Gateway** | Stateless | Ponto de entrada, roteamento, discovery, health check. | 8080 | TCP, UPD ou gRPC |
| **Leader Node** | Stateful | Processar escritas (`SET`) e coordenar replicação. | 9001 | TCP, UPD ou gRPC |
| **Follower Node**| Stateful | Processar leituras (`GET`) e armazenar réplicas. | 9002, 9003...| TCP, UPD ou gRPC |
| **HTML Node** | Stateless | Servir a página de status do sistema. | 9004, 9005...| TCP, UPD ou gRPC |

---

## 🛠 Tecnologias Utilizadas

- **Java 17+**: Linguagem principal.
- **Maven**: Gerenciamento de dependências e build.
- **gRPC & Protobuf**: Protocolo de comunicação de alto desempenho entre os nós internos.
- **Java HTTP Server (comsun)**: Servidor HTTP leve para o API Gateway.
- **Apache JMeter**: Ferramenta para testes de carga e validação de tolerância a falhas.

---

## ✅ Pré-requisitos

- Java Development Kit (JDK) 17 ou superior.
- Apache Maven instalado e configurado.
- Apache JMeter (para executar os testes).

---
## 🚀 Como Executar

O sistema é composto por várias aplicações Java que devem rodar simultaneamente. Siga a ordem abaixo:

### 1. Compilar o Projeto

Na raiz do projeto, execute:
```bash
mvn clean package
```

### 2. Iniciar os Componentes

Abra terminais separados para cada componente e execute os JARs gerados na pasta `target`.

**Terminal 1: API Gateway**
```bash
java -jar gateway/target/gateway-1.0-SNAPSHOT.jar
```
**Terminal 2: Nó Líder**
```bash
java -jar node/target/node-1.0-SNAPSHOT.jar leader 9001
```
**Terminal 3: Nó Seguidor 1**
```bash
java -jar node/target/node-1.0-SNAPSHOT.jar follower 9002
```
**Terminal 4: Nó Seguidor 2**
```bash
java -jar node/target/node-1.0-SNAPSHOT.jar follower 9003
```
**Terminal 5: Nó HTML 1**
```
java -jar html/target/html-1.0-SNAPSHOT.jar 9004
```
**Terminal 6: Nó HTML 2**
```
java -jar html/target/html-1.0-SNAPSHOT.jar 9005
```
(Adicione mais seguidores ou nós HTML conforme necessário em novos terminais).

O sistema estará acessível externamente em http://localhost:8080.

---

## 🧪 Testes de Carga e Resiliência (JMeter)

O projeto foi validado extensivamente utilizando o Apache JMeter para simular alta concorrência e cenários de falha.

**Cenários Testados:**
- **Carga Normal:** Validação do throughput e latência das operações GET (balanceadas) e SET (direcionadas ao líder).
- **Tolerância a Falhas (Líder):** Simulação da queda do nó Líder durante o teste de carga. O sistema deve exibir erros temporários e se recuperar automaticamente assim que o Líder for reiniciado.
- **Tolerância a Falhas (HTML/Seguidores):** Simulação da queda de nós stateless ou de leitura, validando que o Gateway redireciona o tráfego para os nós remanescentes.

**Notas Importantes sobre os Testes:**
- **Gerenciamento de Conexões gRPC:** A implementação utiliza cache de canais gRPC para evitar a criação excessiva de conexões, garantindo estabilidade sob alta carga.
- **Asserções:** Os planos de teste JMeter utilizam *Response Assertions* para validar não apenas o código HTTP 200, mas também o conteúdo da resposta, detectando erros lógicos (ex: `"ERROR: No service node available"`).

---

### 👥 Autor  
[Joadson Ferreira do Nascimento]



