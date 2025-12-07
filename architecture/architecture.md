# Microservices Architectuur - Overzicht

<img width="871" height="621" alt="Architectuur Java Diagram" src="https://github.com/user-attachments/assets/457526f3-6ef9-4a2b-ad78-ed964f971d67" />

##  Services

### **API Gateway**
- Single entry point voor alle requests
- **Sync communicatie** naar alle services

### **Post Service**
- Beheer blogposts (CRUD)
- **Sync**: OpenFeign calls naar Review Service
- **Async**: Luistert naar review events

### **Review Service**
- Goedkeuren/afwijzen van posts
- **Sync**: OpenFeign calls naar Post Service
- **Async**: Publiceert review events naar Message Bus

### **Comment Service**
- Beheer reacties op posts
- **Sync**: OpenFeign calls naar Post Service

##  Infrastructuur

### **Message Bus (RabbitMQ)**
- Asynchrone event-driven communicatie
- Event flow: Review Service → Message Bus → Post Service

### **Eureka**
- Service discovery en registry

### **Config Server**
- Gecentraliseerd configuratiebeheer

## Communicatie

- **Sync (OpenFeign)**: Direct REST calls tussen services via API Gateway
  
- **Async (Message Bus)**: Event publishing/consuming voor notificaties:
  
  - Redacteur keurt post goed/af in **Review Service**
  - Review Service publiceert event naar **Message Bus**
  - **Post Service** luistert naar event
