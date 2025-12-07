# Fullstack Java Project

## Daan Berger (3TIWa)

## Folder structure

- Readme.md
- _architecture_: this folder contains documentation regarding the architecture of your system.
- `docker-compose.yml` : to start the backend (starts all microservices)
- _backend-java_: contains microservices written in java
- _demo-artifacts_: contains images, files, etc that are useful for demo purposes.
- _frontend-web_: contains the Angular webclient

## How to setup and run this application

### Prerequisites
- Docker and Docker Compose installed
- Java 17 or higher
- Maven

### Setup Steps

1. **Start Infrastructure Services**
   
   Create a `.env` file in the project root with the following variables:

   - MYSQL_ROOT_PASSWORD=your_password 
   - RABBITMQ_DEFAULT_USER=your_username 
   - RABBITMQ_DEFAULT_PASS=your_password

      Start MySQL and RabbitMQ:
   ```bash
   docker-compose up -d

2. **Start Microservices**

Run each microservice in the following order:

**Config Service**:
```bash
cd config-service
mvn spring-boot:run
```

```bash 
cd discovery-service
mvn spring-boot:run
```

```bash 
cd gateway-service
mvn spring-boot:run
```

```bash 
cd post-service
mvn spring-boot:run
```

```bash 
cd comment-service
mvn spring-boot:run
```

```bash 
cd review-service
mvn spring-boot:run


