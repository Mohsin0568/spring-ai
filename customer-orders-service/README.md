# Natural Language Search in Spring Boot Using Spring AI Tool Calling 

## Overview

This project demonstrates a production-oriented pattern for implementing natural language search using Spring AI, tool calling, and MongoDB.

Instead of relying on traditional filter-based queries, the application allows users to search data using natural language. The system translates user intent into structured queries and retrieves results from the database.

The implementation emphasizes:

* Separation of intent interpretation and execution
* Deterministic data retrieval using backend logic
* Strongly typed responses instead of free-form text

Although the example focuses on order search, the architecture can be applied to any domain involving structured data.

---

## Architecture

The application follows a layered approach:

1. **Controller Layer**

    * Accepts user input
    * Sends prompt to LLM via Spring AI
    * Configures system message and tools

2. **LLM (Spring AI)**

    * Interprets natural language
    * Decides whether to invoke a tool

3. **Tools Layer**

    * Exposes backend functions to the model
    * Receives structured input from LLM

4. **Service Layer**

    * Builds dynamic MongoDB queries
    * Fetches data from database

5. **Database (MongoDB)**

    * Stores application data

---

## Key Features

* Natural language query support
* Tool calling for controlled execution
* Externalized system prompts (classpath-based)
* Dynamic MongoDB query construction
* Strongly typed API responses
* Clean separation of concerns

---

## Tech Stack

* Java 17+
* Spring Boot
* Spring AI
* MongoDB
* OpenAI API

---

## API Example

### Request

```bash
GET /api/orders?query=Show delivered orders for John from last week
```

### Response

```json
[
  {
    "orderId": "ORD123",
    "customerName": "John",
    "status": "DELIVERED",
    "orderDate": "2024-01-05"
  }
]
```

---

## How It Works

1. User submits a natural language query
2. Controller sends query + system prompt to LLM
3. LLM invokes a tool with structured parameters
4. Tool calls service layer
5. Service queries MongoDB
6. Results are returned as domain objects

---

## Prerequisites

Before running the application, ensure you have:

* Java 17 or higher
* Maven installed
* MongoDB running locally or remotely
* OpenAI API key

---

## Configuration

Set your OpenAI API key in environment variables:

```bash
export OPENAI_API_KEY=your_api_key_here
```

Or in `application.yml`:

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
```

---

## How to Run Locally

### 1. Clone the Repository

```bash
git clone https://github.com/Mohsin0568/spring-ai/tree/main/customer-orders-service
cd customer-orders-service
```

---

### 2. Start MongoDB

If running locally:

```bash
mongod
```

Or use Docker:

```bash
docker run -d -p 27017:27017 --name mongodb mongo
```

---

### 3. Build the Application

```bash
mvn clean install
```

---

### 4. Run the Application

```bash
mvn spring-boot:run
```

Or run the main class from your IDE.

---

### 5. Test the API

Use curl or Postman:

```bash
curl "http://localhost:8080/api/orders?query=Show delivered orders for John"
```

---

## Prompt Templates

System prompts are externalized in:

```
src/main/resources/promptTemplates/
```

This allows:

* Easy modification of model behavior
* Independent iteration without code changes

---

## Future Enhancements

* Pagination support
* Aggregation queries (grouping, summaries)
* Vector search for semantic queries
* Improved date parsing

---

## Author

Mohsin Murtuza
