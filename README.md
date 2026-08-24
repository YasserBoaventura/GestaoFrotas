🚛 Fleet Manager — Sistema de Gestão de Frotas
Sistema backend para gestão completa de frotas de veículos, desenvolvido em Java com Spring Boot e SQL. Permite controlar abastecimentos, viagens, rotas, veículos, motoristas, manutenções, custos, rastreamento em tempo real (tracking) e notificações automáticas por e-mail.
________________________________________
📋 Índice
•	Visão Geral
•	Funcionalidades
•	Arquitetura e Tecnologias
•	Estrutura do Projeto
•	Pré-requisitos
•	Configuração
•	Como Executar 
o	Com Docker (recomendado)
o	Localmente sem Docker
•	Variáveis de Ambiente
•	Documentação da API
•	Autenticação e Recuperação de Senha
•	Tracking em Tempo Real (WebSocket + Google Maps)
•	Alertas Automáticos de Manutenção
•	Testes
•	Contribuição
•	Licença
________________________________________
🧭 Visão Geral
O Fleet Manager é uma API REST construída com Spring Boot cujo objetivo é centralizar toda a operação de uma frota de veículos: desde o cadastro de motoristas e veículos, passando pelo planeamento de rotas e viagens, até ao controlo financeiro de custos muntencoes e abastecimentos. O sistema também oferece rastreamento em tempo real dos veículos através de WebSocket integrado com a Google Maps API, além de um módulo de segurança com recuperação de senha e alertas automáticos por e-mail (SMTP).
________________________________________
⚙️ Funcionalidades
Módulo	Descrição
🚗 Veículos	Cadastro, edição, consulta e desativação de veículos da frota (placa, modelo, marca, ano, capacidade, estado atual, etc.)
👨‍✈️ Motoristas	Gestão de motoristas, associação a veículos/viagens, dados de carta de condução e disponibilidade
🗺️ Rotas	Criação e gestão de rotas fixas ou dinâmicas, com pontos de origem, destino e paragens intermédias
🧳 Viagens	Registo de viagens realizadas, associando motorista, veículo, rota, data/hora de início e fim
⛽ Abastecimentos	Controlo de abastecimentos de combustível por veículo (litros, valor, posto, quilometragem)
🔧 Manutenções	Registo de manutenções preventivas e corretivas, histórico por veículo e agendamento futuro 
💰 Custos	Consolidação de custos operacionais (combustível, manutenção, outros) por veículo, motorista ou período
📍 Tracking em Tempo Real	Rastreamento da localização dos veículos via WebSocket, exibido em mapa usando a Google Maps API
🔐 Segurança e Autenticação	Login, autenticação (JWT), recuperação de senha via código enviado por e-mail (SMTP)
📧 Alertas Automáticos	Envio automático de e-mails de alerta quando uma manutenção estiver próxima ou vencida
________________________________________
🏗️ Arquitetura e Tecnologias
•	Java 21+
•	Spring Boot 3.x 
o	Spring Web (REST API)
o	Spring Data JPA / Hibernate
o	Spring Security + JWT
o	Spring WebSocket (STOMP) — tracking em tempo real
o	Spring Mail (SMTP) — recuperação de senha e alertas
o	Spring Validation
•	Banco de Dados: MySQL (ou PostgreSQL — ajustar conforme o teu application.yml)
•	Agendamento de tarefas: Spring Scheduler (@Scheduled) para verificação diária de manutenções
•	Build: Maven
•	Contentorização: Docker e Docker Compose
•	Documentação da API: Swagger / OpenAPI (springdoc-openapi)
•	Mapas: Google Maps JavaScript API / Directions API (consumida pelo frontend, com dados fornecidos por este backend)
________________________________________
📁 Estrutura do Projeto
GestaoFrotas/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── GestaoRotas/
│   │   │           └── GestaoRotas/
│   │   │
│   │   │               ├── GestaoRotasApplication.java
│   │   │               ├── auth/
│   │   │               ├── authConfigs/
│   │   │               ├── Email/
│   │   │               ├── Custos/
│   │   │               │   └── dto/
│   │   │               ├── Tracking/
│   │   │               ├── Recuperacao_de_Senha/
│   │   │               │   └── CustoDTO/
│   │   │               ├── controller/
│   │   │               ├── dto/
│   │   │               ├── entity/
│   │   │               ├── repository/
│   │   │               ├── service/
│   │   │               ├── model/
│   │   │               │   └── enums/
│   │   │               │
│   │   └── resources/
│   │       ├── application.properties
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── GestaoRotas/
│
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── .gitignore
├── .env.example
└── README.md
________________________________________
✅ Pré-requisitos
•	Docker e Docker Compose instalados
•	(Para execução local sem Docker) Java 21+ e Maven 3.8+
•	Uma conta de e-mail com SMTP habilitado (ex.: Gmail com "senha de app") para envio de e-mails
•	Uma chave de API do Google Maps (Maps JavaScript API / Directions API / Geocoding API, conforme uso)
________________________________________
🔧 Configuração
Antes de executar o projeto, cria um ficheiro .env (ou configura as variáveis de ambiente diretamente) na raiz do projeto, com base no exemplo .env.example abaixo:
# Banco de Dados
DB_HOST=db
DB_PORT=3306
DB_NAME=fleet_manager
DB_USER=fleet_user
DB_PASSWORD=fleet_password

# JWT
JWT_SECRET=coloque_aqui_uma_chave_secreta
JWT_EXPIRATION=3600000

# SMTP (envio de e-mails - recuperação de senha e alertas)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=seuemail@gmail.com
MAIL_PASSWORD=sua_senha_de_app
MAIL_FROM=seuemail@gmail.com

# Google Maps
GOOGLE_MAPS_API_KEY=sua_chave_google_maps

# Aplicação
SERVER_PORT=8080
No application.yml (ou application.properties), estas variáveis devem estar referenciadas assim:
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  mail:
    host: ${MAIL_HOST}
    port: ${MAIL_PORT}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

server:
  port: ${SERVER_PORT}

jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION}

google:
  maps:
    api-key: ${GOOGLE_MAPS_API_KEY}
________________________________________
🚀 Como Executar
🐳 Com Docker (recomendado)
Este é o método mais simples, pois sobe a aplicação e o banco de dados automaticamente.
1. Clona o repositório
git clone https://github.com/YasserBoaventura/GestaoFrotas.git/

cd fleet-manager
2. Cria o ficheiro .env na raiz do projeto com as variáveis mencionadas acima.
3. Sobe os contentores
docker-compose up --build
4. Verifica se está tudo a correr
docker-compose ps
A API estará disponível em: http://localhost:8080
5. Para parar os contentores
docker-compose down
6. Para parar e remover também os volumes (apaga dados do banco)
docker-compose down -v
Exemplo de docker-compose.yml
version: '3.8'

services:

  backend:
    build: ./GestaoRotas
    container_name: gestao_frotas-mysql
    ports:
      - "8080:8080"
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/gestaofrotas
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: Boaventura

  db:
    image: mysql:8
    container_name: gestao_frotas-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: Boaventura
      MYSQL_DATABASE: gestaofrotas
    ports:
      - "3306:3306"


Exemplo de Dockerfile
# Etapa de build

FROM maven:3.9.9-amazoncorretto-21 AS build

WORKDIR /com


COPY pom.xml .


RUN mvn dependency:go-offline


COPY src ./src

RUN mvn clean compile -X


RUN mvn clean package -DskipTests -Dmaven.compiler.failOnError=false

FROM amazoncorretto:21-alpine
WORKDIR /com
COPY --from=build /com/target/*.jar com.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

________________________________________
💻 Localmente sem Docker
1. Configura o banco de dados MySQL/PostgreSQL localmente e cria a base de dados fleet_manager.
2. Configura o application.yml com as tuas credenciais locais.
3. Compila e executa
mvn clean install
mvn spring-boot:run
Ou, gerando o .jar:
mvn clean package -DskipTests
java -jar target/fleet-manager-0.0.1-SNAPSHOT.jar
________________________________________
🔑 Variáveis de Ambiente
Variável	Descrição	Exemplo
DB_HOST	Host do banco de dados	db (docker) / localhost
DB_PORT	Porta do banco de dados	3306
DB_NAME	Nome da base de dados	fleet_manager
DB_USER	Utilizador do banco	fleet_user
DB_PASSWORD	Senha do banco	********
JWT_SECRET	Chave secreta para geração dos tokens JWT	********
JWT_EXPIRATION	Tempo de expiração do token (ms)	3600000
MAIL_HOST	Servidor SMTP	smtp.gmail.com
MAIL_PORT	Porta SMTP	587
MAIL_USERNAME	E-mail usado para envio	seuemail@gmail.com
MAIL_PASSWORD	Senha/senha de app do e-mail	********
GOOGLE_MAPS_API_KEY	Chave da API do Google Maps	AIza...
SERVER_PORT	Porta em que a aplicação sobe	8080
________________________________________
📚 Documentação da API
Com o projeto a correr, a documentação interativa (Swagger UI) fica disponível em:
http://localhost:9001/swagger-ui/index.html
E o JSON do OpenAPI em:
http://localhost:9001/v3/api-docs
Principais endpoints: 
Método	Endpoint	Descrição
POST	/api/auth/login	Autenticação de utilizador
POST	/api/auth/recuperar-senha	Solicita código de recuperação de senha
POST	/api/auth/redefinir-senha	Redefine a senha com o código recebido
GET/POST	/api/veiculos	Listar / cadastrar veículos
GET/POST	/api/motoristas	Listar / cadastrar motoristas
GET/POST	/api/rotas	Listar / cadastrar rotas
GET/POST	/api/viagens	Listar / cadastrar viagens
GET/POST	/api/abastecimentos	Listar / cadastrar abastecimentos
GET/POST	/api/manutencoes	Listar / cadastrar manutenções
GET	/api/custos	Relatórios de custos consolidados
WS	/ws/tracking	Canal WebSocket para localização em tempo real
Adapta esta tabela aos nomes reais dos teus @RequestMapping.
________________________________________
🔐 Autenticação e Recuperação de Senha
O sistema utiliza JWT para autenticação das requisições. O fluxo de recuperação de senha funciona assim:
1.	O utilizador solicita a recuperação informando o e-mail (POST /api/auth/recuperar-senha);
2.	O sistema gera um código temporário e o envia por e-mail via SMTP (Spring Mail);
3.	O utilizador informa o código recebido junto com a nova senha (POST /api/auth/redefinir-senha);
4.	O sistema valida o código (com tempo de expiração) e atualiza a senha, criptografada com BCrypt.
________________________________________
📍 Tracking em Tempo Real (WebSocket + Google Maps)
•	O backend expõe um endpoint WebSocket (protocolo STOMP) onde os veículos (ou dispositivos GPS a bordo) enviam periodicamente a sua localização (latitude/longitude).
•	Essas coordenadas são processadas e retransmitidas em tempo real para os clientes inscritos (ex.: painel administrativo).
•	No frontend, a Google Maps API é usada para plotar a posição dos veículos no mapa em tempo real, atualizando o marcador conforme os dados chegam pelo WebSocket.
Exemplo de fluxo:
Dispositivo GPS/Veículo → POST/WS /ws/tracking → Broker STOMP → Frontend (subscrito no tópico) → Atualiza marcador no Google Maps
________________________________________
📧 Alertas Automáticos de Manutenção
Uma tarefa agendada (@Scheduled) roda periodicamente (ex.: diariamente) e verifica:
•	Veículos com manutenção próxima (baseado em data ou quilometragem);
•	Veículos com manutenção vencida.
Quando identificados, o sistema envia automaticamente um e-mail de alerta (via SMTP) para o(s) responsável(is) cadastrado(s), informando o veículo, o tipo de manutenção e o prazo.
________________________________________
🧪 Testes
mvn test
Para rodar com cobertura (se configurado com JaCoCo):
mvn test jacoco:report
________________________________________
🤝 Contribuição
1.	Faz um fork do projeto
2.	Cria uma branch para a tua feature (git checkout -b feature/minha-feature)
3.	Faz commit das tuas alterações (git commit -m 'Adiciona minha feature')
4.	Faz push para a branch (git push origin feature/minha-feature)
5.	Abre um Pull Request
________________________________________
📄 Licença
Este projeto está sob a licença MIT — sinta-se à vontade para usar, modificar e distribuir.
________________________________________
Desenvolvido com ☕ e Spring Boot.

