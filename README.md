# Spring Boot GraphQL Book Management System

A modern book management system built with Spring Boot and GraphQL, demonstrating how to implement Query by Example (QBE) pattern with JPA repositories.

## Overview

This project showcases a robust implementation of a GraphQL API for managing books, featuring:

- GraphQL API with flexible querying capabilities
- Spring Data JPA with Query by Example support
- PostgreSQL database integration
- Docker Compose for local development
- Comprehensive test coverage using TestContainers

## Project Requirements

- Java 23
- Docker and Docker Compose
- Maven 3.9.x
- PostgreSQL 16

## Dependencies

Main dependencies included in this project:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-graphql</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

## Getting Started

1. Ensure Docker is running on your system
2. The application will automatically start PostgreSQL using Docker Compose with the following configuration:
```yaml
services:
  postgres:
    image: 'postgres:latest'
    environment:
      - 'POSTGRES_DB=graphql_books'
      - 'POSTGRES_PASSWORD=password'
      - 'POSTGRES_USER=user'
    ports:
      - '5432:5432'
```

## Running the Application

1. Start the application:
```bash
./mvnw spring-boot:run
```

2. Access GraphiQL interface at: http://localhost:8080/graphiql

## GraphQL API Usage

The API supports the following queries:

### Query all books
```graphql
query {
    books {
        id
        title
        author
        publishedYear
    }
}
```

### Query book by ID
```graphql
query {
    book(id: "1") {
        title
        author
        publishedYear
    }
}
```

### Query books using example
```graphql
query {
    books(book: {
        author: "Craig Walls"
        publishedYear: 2022
    }) {
        id
        title
        author
        publishedYear
    }
}
```

## Data Model

The Book entity contains the following fields:

```java
public class Book {
    private Long id;
    private String title;
    private String author;
    private Integer publishedYear;
}
```

## Testing

The project includes comprehensive integration tests using TestContainers. Key test cases:

- Finding all books
- Finding books by ID
- Finding books by example (QBE)
- Testing with no matches
- Author pattern matching

Run tests using:
```bash
./mvnw test
```

## Development Features

- GraphiQL interface enabled for development
- Automatic schema generation
- JPA show-sql enabled for debugging
- Spring DevTools for rapid development

## Configuration

Key application properties:

```properties
spring.application.name=graphql-qbe
spring.graphql.graphiql.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

## Additional Resources

- [Spring GraphQL Documentation](https://docs.spring.io/spring-graphql/docs/current/reference/html/)
- [GraphQL Java Documentation](https://www.graphql-java.com/documentation/getting-started)
- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)