# 🚛 Fleet Manager — Sistema de Gestão de Frotas

Sistema backend para **gestão completa de frotas de veículos**, desenvolvido com **Java 21, Spring Boot e MySQL**.

O Fleet Manager permite centralizar a gestão de veículos, motoristas, viagens, rotas, abastecimentos, manutenções e custos operacionais. O sistema também possui **autenticação JWT, recuperação de senha por e-mail, alertas automáticos de manutenção e rastreamento de veículos em tempo real utilizando WebSocket e Google Maps**.

---

## 📋 Índice

* [📖 Visão Geral](#-visão-geral)
* [⚙️ Funcionalidades](#️-funcionalidades)
* [🏗️ Arquitetura e Tecnologias](#️-arquitetura-e-tecnologias)
* [📁 Estrutura do Projeto](#-estrutura-do-projeto)
* [✅ Pré-requisitos](#-pré-requisitos)
* [🔧 Configuração](#-configuração)
* [🚀 Como Executar](#-como-executar)

  * [🐳 Com Docker](#-com-docker)
  * [💻 Sem Docker](#-sem-docker)
* [🔐 Autenticação](#-autenticação)
* [📍 Tracking em Tempo Real](#-tracking-em-tempo-real)
* [📧 Alertas Automáticos](#-alertas-automáticos)
* [📚 Documentação da API](#-documentação-da-api)
* [🧪 Testes](#-testes)
* [🤝 Contribuição](#-contribuição)
* [📄 Licença](#-licença)

---

## 📖 Visão Geral

O **Fleet Manager** é uma API REST desenvolvida com **Spring Boot** para centralizar e automatizar a gestão operacional de uma frota de veículos.

A plataforma permite controlar todo o ciclo operacional da frota, desde o cadastro de veículos e motoristas até ao acompanhamento de viagens, manutenção, abastecimento e custos.

Além da API REST, o sistema disponibiliza recursos de **comunicação em tempo real através de WebSocket**, permitindo acompanhar a localização dos veículos num mapa.

### Principais objetivos

* Centralizar informações da frota;
* Reduzir o controlo manual das operações;
* Monitorizar veículos e motoristas;
* Controlar custos operacionais;
* Acompanhar manutenções;
* Monitorizar abastecimentos;
* Acompanhar veículos em tempo real;
* Automatizar alertas de manutenção;
* Garantir segurança através de autenticação JWT.

---

# ⚙️ Funcionalidades

| Módulo                      | Descrição                                                                                                       |
| --------------------------- | --------------------------------------------------------------------------------------------------------------- |
| 🚗 **Veículos**             | Cadastro, edição, consulta e desativação de veículos, incluindo placa, marca, modelo, ano, capacidade e estado. |
| 👨‍✈️ **Motoristas**        | Gestão de motoristas, dados da carta de condução, disponibilidade e associação com veículos e viagens.          |
| 🗺️ **Rotas**               | Criação e gestão de rotas, incluindo origem, destino e pontos intermédios.                                      |
| 🧳 **Viagens**              | Registo e acompanhamento de viagens, relacionando veículos, motoristas e rotas.                                 |
| ⛽ **Abastecimentos**        | Registo de abastecimentos, incluindo litros, valor, posto e quilometragem.                                      |
| 🔧 **Manutenções**          | Gestão de manutenções preventivas e corretivas, histórico e agendamento.                                        |
| 💰 **Custos**               | Gestão e consolidação de custos operacionais da frota.                                                          |
| 📍 **Tracking**             | Rastreamento da localização dos veículos em tempo real através de WebSocket.                                    |
| 🔐 **Autenticação**         | Login e proteção dos endpoints utilizando Spring Security e JWT.                                                |
| 🔑 **Recuperação de senha** | Recuperação de senha através de código temporário enviado por e-mail.                                           |
| 📧 **Alertas automáticos**  | Envio automático de e-mails relacionados com manutenções próximas ou vencidas.                                  |
| 📊 **Relatórios**           | Geração de relatórios relacionados com custos e operações da frota.                                             |

---

# 🏗️ Arquitetura e Tecnologias

## Backend

* ☕ **Java 21**
* 🌱 **Spring Boot 3.x**
* 🌐 **Spring Web**
* 🗄️ **Spring Data JPA**
* 🔄 **Hibernate**
* 🔐 **Spring Security**
* 🎫 **JWT**
* 🔌 **Spring WebSocket**
* 📧 **Spring Mail**
* ✅ **Spring Validation**
* ⏰ **Spring Scheduler**

## Banco de Dados

* 🐬 **MySQL 8**
* PostgreSQL pode ser utilizado com as devidas configurações.

## Ferramentas

* 📦 **Maven**
* 🐳 **Docker**
* 🐳 **Docker Compose**
* 📖 **Swagger / OpenAPI**
* 🌎 **Google Maps API**
* 🔀 **Git / GitHub**

---

# 📁 Estrutura do Projeto

```text
GestaoFrotas/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── GestaoRotas/
│   │   │           └── GestaoRotas/
│   │   │               │
│   │   │               ├── auth/
│   │   │               ├── authConfigs/
│   │   │               ├── Email/
│   │   │               ├── Custos/
│   │   │               ├── Tracking/
│   │   │               ├── Recuperacao_de_Senha/
│   │   │               ├── controller/
│   │   │               ├── dto/
│   │   │               ├── entity/
│   │   │               ├── repository/
│   │   │               ├── service/
│   │   │               └── model/
│   │   │                   └── enums/
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── GestaoRotas/
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── .env.example
├── .gitignore
└── README.md
```

---

# ✅ Pré-requisitos

Antes de executar o projeto, certifique-se de possuir:

### Com Docker

* Docker
* Docker Compose
* Git

### Sem Docker

* Java 21+
* Maven 3.8+
* MySQL 8+ ou PostgreSQL
* Git

### Serviços externos

Para utilizar todas as funcionalidades do sistema:

* Conta de e-mail com SMTP habilitado;
* Google Maps API Key.

---

# 🔧 Configuração

O projeto utiliza **variáveis de ambiente** para evitar a exposição de informações sensíveis, como credenciais do banco de dados, senha SMTP e chave JWT.

Crie um arquivo `.env` na raiz do projeto com base no `.env.example`.

```env
# ==============================
# BANCO DE DADOS
# ==============================

DB_HOST=db
DB_PORT=3306
DB_NAME=fleet_manager
DB_USER=fleet_user
DB_PASSWORD=fleet_password


# ==============================
# JWT
# ==============================

JWT_SECRET=coloque_uma_chave_secreta_forte
JWT_EXPIRATION=3600000


# ==============================
# SMTP
# ==============================

MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=seuemail@gmail.com
MAIL_PASSWORD=sua_senha_de_app
MAIL_FROM=seuemail@gmail.com


# ==============================
# GOOGLE MAPS
# ==============================

GOOGLE_MAPS_API_KEY=sua_chave_google_maps


# ==============================
# APLICAÇÃO
# ==============================

SERVER_PORT=8080
```

> ⚠️ **Importante:** nunca faça commit do arquivo `.env`. Utilize `.env.example` para disponibilizar apenas as variáveis necessárias, sem credenciais reais.

---

# 🚀 Como Executar

## 🐳 Com Docker

A execução utilizando Docker é a forma recomendada, pois permite executar a aplicação juntamente com o banco de dados.

### 1. Clonar o repositório

```bash
git clone https://github.com/YasserBoaventura/GestaoFrotas.git
```

```bash
cd GestaoFrotas
```

### 2. Criar o arquivo `.env`

Crie o arquivo:

```text
.env
```

e configure as variáveis apresentadas na seção [Configuração](#-configuração).

### 3. Construir e iniciar os containers

```bash
docker compose up --build
```

### 4. Verificar os containers

```bash
docker compose ps
```

Se tudo estiver configurado corretamente, a API estará disponível em:

```text
http://localhost:8080
```

### 5. Parar os containers

```bash
docker compose down
```

### 6. Parar os containers e remover os volumes

> ⚠️ Este comando remove também os dados persistidos do banco de dados.

```bash
docker compose down -v
```

---

# 🐳 Docker Compose

Exemplo de configuração:

```yaml
services:

  backend:
    build: .
    container_name: fleet-manager-backend
    ports:
      - "8080:8080"
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/fleet_manager
      SPRING_DATASOURCE_USERNAME: fleet_user
      SPRING_DATASOURCE_PASSWORD: fleet_password

  db:
    image: mysql:8
    container_name: fleet-manager-db
    restart: always
    environment:
      MYSQL_DATABASE: fleet_manager
      MYSQL_USER: fleet_user
      MYSQL_PASSWORD: fleet_password
      MYSQL_ROOT_PASSWORD: root_password
    ports:
      - "3306:3306"
```

---

# 🐋 Dockerfile

Exemplo de Dockerfile utilizando **Java 21 + Maven**:

```dockerfile
# ==============================
# Build
# ==============================

FROM maven:3.9.9-amazoncorretto-21 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests


# ==============================
# Runtime
# ==============================

FROM amazoncorretto:21-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

# 💻 Sem Docker

Também é possível executar o projeto diretamente na máquina.

### 1. Criar o banco de dados

No MySQL:

```sql
CREATE DATABASE gestaofrotas;
```

Configure as credenciais correspondentes no ambiente da aplicação.

### 2. Compilar o projeto

```bash
mvn clean install
```

### 3. Executar com Spring Boot

```bash
mvn spring-boot:run
```

### 4. Ou executar o JAR

```bash
mvn clean package -DskipTests
```

Depois:

```bash
java -jar target/*.jar
```

---

# 🔑 Variáveis de Ambiente

| Variável              | Descrição                                  | Exemplo              |
| --------------------- | ------------------------------------------ | -------------------- |
| `DB_HOST`             | Host do banco de dados                     | `db`                 |
| `DB_PORT`             | Porta do banco                             | `3306`               |
| `DB_NAME`             | Nome da base de dados                      | `fleet_manager`      |
| `DB_USER`             | Utilizador do banco                        | `fleet_user`         |
| `DB_PASSWORD`         | Senha do banco                             | `********`           |
| `JWT_SECRET`          | Chave utilizada para assinar tokens JWT    | `********`           |
| `JWT_EXPIRATION`      | Tempo de expiração do JWT em milissegundos | `3600000`            |
| `MAIL_HOST`           | Servidor SMTP                              | `smtp.gmail.com`     |
| `MAIL_PORT`           | Porta SMTP                                 | `587`                |
| `MAIL_USERNAME`       | E-mail utilizado pelo sistema              | `seuemail@gmail.com` |
| `MAIL_PASSWORD`       | Senha ou App Password do e-mail            | `********`           |
| `MAIL_FROM`           | Remetente dos e-mails                      | `seuemail@gmail.com` |
| `GOOGLE_MAPS_API_KEY` | Chave da Google Maps API                   | `AIza...`            |
| `SERVER_PORT`         | Porta da aplicação                         | `8080`               |

---

# 📚 Documentação da API

O projeto utiliza **Swagger / OpenAPI** para documentação e testes dos endpoints.

Com a aplicação em execução, acesse:

```text
http://localhost:9001/swagger-ui/index.html
```

Documentação OpenAPI:

```text
http://localhost:9001/v3/api-docs
```

> Ajuste a porta caso a aplicação esteja configurada para utilizar outra porta.

---

# 🔌 Principais Endpoints

| Método     | Endpoint                    | Descrição                               |
| ---------- | --------------------------- | --------------------------------------- |
| `POST`     | `/api/auth/login`           | Autenticação do utilizador              |
| `POST`     | `/api/auth/recuperar-senha` | Solicitação de recuperação de senha     |
| `POST`     | `/api/auth/redefinir-senha` | Redefinição da senha                    |
| `GET/POST` | `/api/veiculos`             | Consultar e cadastrar veículos          |
| `GET/POST` | `/api/motoristas`           | Consultar e cadastrar motoristas        |
| `GET/POST` | `/api/rotas`                | Consultar e cadastrar rotas             |
| `GET/POST` | `/api/viagens`              | Consultar e cadastrar viagens           |
| `GET/POST` | `/api/abastecimentos`       | Consultar e cadastrar abastecimentos    |
| `GET/POST` | `/api/manutencoes`          | Consultar e cadastrar manutenções       |
| `GET`      | `/api/custos`               | Consultar custos e relatórios           |
| `WS`       | `/ws/tracking`              | Comunicação em tempo real para tracking |

> Os endpoints acima devem ser mantidos sincronizados com os `@RequestMapping` e `@GetMapping`, `@PostMapping`, etc. existentes no código.

---

# 🔐 Autenticação

O sistema utiliza **Spring Security + JWT** para proteger os recursos da API.

### Fluxo de autenticação

```text
Cliente
   │
   ▼
POST /api/auth/login
   │
   ▼
Spring Security
   │
   ▼
Validação das credenciais
   │
   ▼
JWT Token
   │
   ▼
Cliente
   │
   ▼
Authorization: Bearer <token>
   │
   ▼
API protegida
```

As senhas dos utilizadores são armazenadas utilizando **BCrypt**, evitando o armazenamento de senhas em texto simples.

---

# 🔑 Recuperação de Senha

O sistema possui um fluxo de recuperação de senha através de código temporário enviado por e-mail.

### Fluxo

```text
Utilizador
    │
    ▼
Solicita recuperação
    │
    ▼
Sistema gera código
    │
    ▼
Código enviado por SMTP
    │
    ▼
Utilizador informa código
    │
    ▼
Sistema valida código
    │
    ▼
Nova senha
    │
    ▼
Senha armazenada com BCrypt
```

Endpoints:

```text
POST /api/auth/recuperar-senha
POST /api/auth/redefinir-senha
```

---

# 📍 Tracking em Tempo Real

O Fleet Manager possui um módulo de **rastreamento em tempo real** utilizando **Spring WebSocket com STOMP**.

As coordenadas dos veículos podem ser transmitidas para o backend e posteriormente distribuídas aos clientes conectados.

### Fluxo

```text
GPS / Dispositivo
       │
       ▼
WebSocket
       │
       ▼
Spring Boot
       │
       ▼
Broker STOMP
       │
       ▼
Frontend
       │
       ▼
Google Maps
       │
       ▼
📍 Localização do veículo
```

As informações de localização podem incluir:

* Latitude;
* Longitude;
* Velocidade;
* Horário;
* Estado do veículo;
* Histórico de localização.

O frontend utiliza a **Google Maps API** para representar os veículos no mapa.

---

# 📧 Alertas Automáticos de Manutenção

O sistema utiliza o **Spring Scheduler (`@Scheduled`)** para executar verificações automáticas.

Periodicamente, o sistema verifica:

* 🔧 Manutenções próximas;
* ⚠️ Manutenções vencidas;
* 🚗 Veículos que necessitam de manutenção;
* 📅 Próximos prazos de manutenção.

Quando uma condição é identificada, o sistema pode enviar automaticamente um e-mail para os responsáveis.

### Fluxo

```text
@Scheduled
    │
    ▼
Verifica manutenções
    │
    ├── Manutenção próxima
    │
    └── Manutenção vencida
            │
            ▼
       Spring Mail
            │
            ▼
        📧 E-mail
```

---

# 🧪 Testes

Para executar os testes automatizados:

```bash
mvn test
```

Caso o projeto esteja configurado com **JaCoCo**, é possível gerar o relatório de cobertura:

```bash
mvn test jacoco:report
```

---

# 🤝 Contribuição

Contribuições são bem-vindas.

### 1. Faça um fork

```bash
git fork
```

### 2. Crie uma branch

```bash
git checkout -b feature/minha-feature
```

### 3. Faça as alterações

Implemente a funcionalidade ou correção desejada.

### 4. Faça o commit

```bash
git add .
```

```bash
git commit -m "feat: adiciona minha feature"
```

### 5. Faça o push

```bash
git push origin feature/minha-feature
```

### 6. Abra um Pull Request

Envie um Pull Request para o repositório principal.

---

# 📄 Licença

Este projeto está disponível sob a licença **MIT**.

Você pode utilizar, modificar e distribuir o projeto de acordo com os termos da licença.

---

## 👨‍💻 Desenvolvedor

Desenvolvido com ☕ **Java + Spring Boot**.

**Yasser Boaventura**

GitHub:
https://github.com/YasserBoaventura

---

## ⭐ Fleet Manager

Se este projeto foi útil para você, considere deixar uma ⭐ no repositório.

**Java • Spring Boot • Spring Security • JWT • MySQL • WebSocket • Docker • Google Maps**
