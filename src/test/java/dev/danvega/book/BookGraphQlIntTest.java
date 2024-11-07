package dev.danvega.book;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureHttpGraphQlTester
@Testcontainers
public class BookGraphQlIntTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));

    @Autowired
    private HttpGraphQlTester graphQlTester;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        bookRepository.saveAll(List.of(
                new Book("Spring in Action", "Craig Walls", 2022),
                new Book("Spring Boot in Practice", "Somnath Musib", 2023),
                new Book("Learning GraphQL", "Eve Porcello", 2023),
                new Book("Java in Action", "Raoul-Gabriel Urma", 2021)
        ));
    }

    @Test
    void testFindAll() {
        String query = """
            query {
                books {
                    id
                    title
                    author
                    publishedYear
                }
            }
        """;

        graphQlTester.document(query)
                .execute()
                .path("data.books")
                .entityList(Book.class)
                .hasSize(4);
    }

    @Test
    void testFindById() {
        Book savedBook = bookRepository.findAll().get(0);

        String query = """
            query($id: ID!) {
                book(id: $id) {
                    id
                    title
                    author
                    publishedYear
                }
            }
        """;

        graphQlTester.document(query)
                .variable("id", savedBook.getId().toString())
                .execute()
                .path("data.book")
                .entity(Book.class)
                .satisfies(book -> {
                    assert book.getId().equals(savedBook.getId());
                    assert book.getTitle().equals(savedBook.getTitle());
                });
    }

    @Test
    void testFindByExample_ExactTitle() {
        String document = """
            query($bookInput: BookInput!) {
                books(book: $bookInput) {
                    id
                    title
                    author
                    publishedYear
                }
            }
        """;

        graphQlTester.document(document)
                .variable("bookInput", Map.of("title", "Spring in Action"))
                .execute()
                .path("data.books")
                .entityList(Book.class)
                .hasSize(1)
                .satisfies(books -> {
                    assert books.get(0).getTitle().equals("Spring in Action");
                });
    }

    @Test
    void testFindByExample_PublishedYearAndAuthorPattern() {
        String document = """
            query($bookInput: BookInput!) {
                books(book: $bookInput) {
                    id
                    title
                    author
                    publishedYear
                }
            }
        """;

        graphQlTester.document(document)
                .variable("bookInput", Map.of("publishedYear",2023,"author","Eve Porcello"))
                .execute()
                .path("data.books")
                .entityList(Book.class)
                .hasSize(1)
                .satisfies(books -> {
                    assert books.get(0).getPublishedYear().equals(2023);
                    assert books.get(0).getAuthor().contains("Eve Porcello");
                    assert books.get(0).getTitle().equals("Learning GraphQL");
                });
    }


    @Test
    void testFindByExample_NoMatch() {
        String document = """
            query($bookInput: BookInput!) {
                books(book: $bookInput) {
                    id
                    title
                    author
                    publishedYear
                }
            }
        """;

        graphQlTester.document(document)
                .variable("bookInput", Map.of("title","Non Existent Book"))
                .execute()
                .path("data.books")
                .entityList(Book.class)
                .hasSize(0);
    }

}
