# Spring Boot GraphQL Book Management System

A modern book management system built with Spring Boot and GraphQL, demonstrating how to implement Query by Example (QBE) pattern with JPA repositories.

## Overview

This project showcases a robust implementation of a GraphQL API for managing books, featuring:

- GraphQL API with flexible querying capabilities
- Spring Data JPA with Query by Example support
- PostgreSQL database integration
- Docker Compose for local development
- Comprehensive test coverage using TestContainers

## Why Query by Example (QBE)?

Query by Example significantly simplifies dynamic querying in your application. Here's a comparison of implementing the same search functionality with and without QBE:

### Traditional Approach (Without QBE)

```java
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;
    
    @GetMapping("/search")
    public List<Book> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer publishedYear) {
        
        return bookRepository.findAll(new Specification<Book>() {
            @Override
            public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query, 
                                       CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                
                if (title != null) {
                    predicates.add(cb.like(root.get("title"), "%" + title + "%"));
                }
                if (author != null) {
                    predicates.add(cb.like(root.get("author"), "%" + author + "%"));
                }
                if (publishedYear != null) {
                    predicates.add(cb.equal(root.get("publishedYear"), publishedYear));
                }
                
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        });
    }
}
```

You can also write custom repository methods but this gets cluttered when you want to support every possible combination. 

```java
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Basic search methods
    List<Book> findByTitle(String title);
    List<Book> findByAuthor(String author);
    List<Book> findByPublishedYear(Integer year);
    
    // Partial matches
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    // Multiple criteria - exact matches
    List<Book> findByTitleAndAuthor(String title, String author);
    List<Book> findByTitleAndPublishedYear(String title, Integer year);
    List<Book> findByAuthorAndPublishedYear(String author, Integer year);
    List<Book> findByTitleAndAuthorAndPublishedYear(String title, String author, Integer year);
    
    // Multiple criteria - partial matches
    List<Book> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(String title, String author);
    List<Book> findByTitleContainingIgnoreCaseAndPublishedYear(String title, Integer year);
    List<Book> findByAuthorContainingIgnoreCaseAndPublishedYear(String author, Integer year);
    List<Book> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndPublishedYear(
        String title, String author, Integer year);
    
    // Ordered results
    List<Book> findByPublishedYearOrderByTitleAsc(Integer year);
    List<Book> findByAuthorOrderByPublishedYearDesc(String author);
    List<Book> findByTitleContainingIgnoreCaseOrderByPublishedYearDesc(String title);
    
    // Complex queries with @Query
    @Query("SELECT b FROM Book b WHERE b.publishedYear >= :startYear AND b.publishedYear <= :endYear")
    List<Book> findBooksInYearRange(@Param("startYear") Integer startYear, @Param("endYear") Integer endYear);
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> findByTitleOrAuthorContaining(@Param("keyword") String keyword);
    
    // Pagination support
    Page<Book> findByPublishedYear(Integer year, Pageable pageable);
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);
    
    // Count queries
    Long countByPublishedYear(Integer year);
    Long countByAuthor(String author);
    
    // Exists queries
    boolean existsByTitleIgnoreCase(String title);
    boolean existsByAuthorAndPublishedYear(String author, Integer year);
    
    // Delete queries
    void deleteByPublishedYearBefore(Integer year);
    
    // Custom specification for complex dynamic queries
    default List<Book> findByCustomCriteria(String title, String author, Integer yearFrom, Integer yearTo) {
        return findAll((Specification<Book>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), 
                    "%" + title.toLowerCase() + "%"));
            }
            
            if (author != null && !author.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("author")), 
                    "%" + author.toLowerCase() + "%"));
            }
            
            if (yearFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("publishedYear"), yearFrom));
            }
            
            if (yearTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("publishedYear"), yearTo));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
}
```

### With Query by Example

```java
@GraphQlRepository
public interface BookRepository extends JpaRepository<Book, Long>,
QueryByExampleExecutor<Book> {
}

// GraphQL Query
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


**Benefits of QBE**

- Reduced Boilerplate: Eliminates the need for complex specification classes or multiple repository methods
- Type Safety: Provides compile-time type checking for your queries
- Flexible Querying: Easily handle any combination of search criteria without writing custom methods
- GraphQL Integration: Naturally fits with GraphQL's flexible query structure
- Maintainable Code: Less code to maintain and test
- Dynamic Queries: Handle multiple search parameters without complex conditional logic

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