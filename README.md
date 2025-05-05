# Email Notification Event

## Descrição
- Um sistema de microsserviços que realiza o cadastro de usuários em eventos e envia notificações por e-mail de forma assíncrona via RabbitMQ.
- Este projeto é composto por dois microsserviços:
events-ms: serviço que realiza o cadastro de eventos e gerencia as inscrições do usuário com envio de uma mensagem via RabbitMQ (utilizando o brocker Cloud AMQP).
email-ms: serviço que consome as mensagens da fila e envia e-mails aos usuários por meio do SMTP Gmail.
- Quando um novo usuário é criado pelo serviço ms-user, uma mensagem é enviada de via RabbitMQ (utilizando o broker Cloud AMQP). O serviço ms-email consome essa mensagem e envia um e-mail de boas-vindas por meio do servidor SMTP do Gmail.

## Tecnologias Utilizadas
- Linguaguem: Java 17
- Framework: Spring Boot 3.4.5
- Spring Web
- Spring Data JPA
- Validation
- Gerenciador de Dependências: Maven
- Banco de Dados: PostgreSQL
- Mensageria: RabbitMQ
- Broker na Nuvem: Cloud AMQP
- Serviço de E-mail: SMTP Gmail

## Endpoint
```
- POST /events: Criar um novo Evento
```
```
- POST /events/{eventId}/register: Registrar um Usuário em um Evento
```
```
- GET /events: Retorna todos os Eventos disponíveis
```
```
- GET /events/upcoming: Retorna todos os Eventos que ainda vão acontecer
```

## Configuração e Execução

Pré-requisitos: Java 17 e Maven

  1. clone o repositório
  2. acesse o diretório do projeto
  3. configure o banco de dados no application.properties
  4. configure os dados de acesso ao RabbitMQ (Cloud AMQP)
  5. configure as credenciais do servidor de e-mail (SMTP Gmail)

```
# instale as dependências do Maven
mvn clean install

# execute a aplicação
mvn spring-boot:run

# pressione (ctrl + c) para parar a aplicação
```
