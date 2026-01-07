# Chat Backend Service

A WebSocket-based chat backend with message versioning and strict ordering, built with **Java 25**, **Spring Boot 4**, and **PostgreSQL 18**.

Supports:
- Create, edit, and retrieve chat messages
- Optimistic versioning
- Strict message ordering per chat
- Local development using Docker and docker-compose

---

## 🏗️ Project Structure

```ms-message/
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  └─ resources/
│  └─ test/java/
├─ build.gradle
├─ Dockerfile
├─ docker-compose.yml
├─ .dockerignore
└─ README.md
```

---

## 🚀 Prerequisites

- Docker >= 24.x  
- Docker Compose >= 2.x  
- Java 25 (for local build, optional if using Docker)  

---

## 🐳 Run Locally with Docker Compose

This will start **PostgreSQL** and the **chat backend**:

```bash
git clone https://github.com/Gasimzade09/ms-message
cd ms-message
docker-compose up --build

```

## ⚡ Using the Docker Hub Image

The backend image is already available on Docker Hub:

services:
postgres:
image: postgres:18
environment:
POSTGRES_DB: chatdb
POSTGRES_USER: chatuser
POSTGRES_PASSWORD: chatpass
ports:
- "5432:5432"

ms-message:
image: javadevali/ms-message:latest
depends_on:
- postgres
environment:
- SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/rockup
- SPRING_DATASOURCE_USERNAME: rockup
- SPRING_DATASOURCE_PASSWORD: 123456
- ports: "8080:8080"


### Start everything:

``` docker-compose up ```

## Access the WebSocket server:
```ws://localhost:8080/ws/chat```

## 💬 Example WebSocket Request
### Create a message:
```
{
    "type": "CREATE_MESSAGE",
    "chatId": "11111111-1111-1111-1111-111111111111",
    "userId": "22222222-2222-2222-2222-222222222222",
    "payload": "Hello World"
}
```

### Expected Response:
```
{
  "status": "MESSAGE_CREATED",
  "payload": "Hello World",
  "id": "uuid",
  "messageChatN": 1,
  "version": 0
}

```

### Edit message:
```
{
  "type": "EDIT_MESSAGE",
  "messageId": "4da2d7de-dd9c-4fc2-9e3a-754c28e4fa9b",
  "version": 0,
  "payload": "Edited text"
}
```

### Expected Response:
```
{
    "payload": "Edited text",
    "id": "4da2d7de-dd9c-4fc2-9e3a-754c28e4fa9b",
    "messageChatN": 1,
    "version": 1
}
```

### Get messages:
```
{
  "type": "GET_MESSAGES",
  "chatId": "11111111-1111-1111-1111-111111111111",
  "userId":"dasdadsasd"
  "limit": 10,
  "offset": 0
}
```

### Expected messages:
```
{
    "data": [
        {
            "payload": "Hello world! new",
            "id": "6fa7f09e-6e86-4064-acab-cb8280b48fd2",
            "messageChatN": 1,
            "version": 2
        },
        {
            "payload": "Hello world",
            "id": "59270150-c36e-4543-94af-4fb49ba16c6e",
            "messageChatN": 2,
            "version": 0
        },
        {
            "payload": "Hello world",
            "id": "34fb3571-c526-477e-b6a5-84dd07e10268",
            "messageChatN": 3,
            "version": 0
        },
        {
            "payload": "Hello world",
            "id": "80d0d5a6-6aea-4b7a-940d-7bb1cd91cdf9",
            "messageChatN": 4,
            "version": 0
        },
        {
            "payload": "Hello world",
            "id": "9e073dda-798a-4449-bf51-128e0b5fc471",
            "messageChatN": 5,
            "version": 0
        }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 5
}
```



