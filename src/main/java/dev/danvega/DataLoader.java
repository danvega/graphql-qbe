package dev.danvega;

import dev.danvega.book.Book;
import dev.danvega.book.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final BookRepository repository;

    public DataLoader(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        repository.save(new Book("Spring Boot Up & Running", "Mark Heckler", 2021));
        repository.save(new Book("Cloud Native Spring in Action", "Thomas Vitale", 2022));
        repository.save(new Book("Spring Security in Action", "Laurentiu Spilca", 2020));
        repository.save(new Book("Spring Boot in Practice", "Somnath Musib", 2022));
        repository.save(new Book("Pro Spring 5", "Iuliana Cosmina", 2017));
        repository.save(new Book("Spring in Action", "Craig Walls", 2018));
        repository.save(new Book("Spring Microservices in Action", "John Carnell", 2017));
        repository.save(new Book("Java: The Complete Reference", "Herbert Schildt", 2018));
        repository.save(new Book("Effective Java", "Joshua Bloch", 2018));
        repository.save(new Book("Java Concurrency in Practice", "Brian Goetz", 2006));
        repository.save(new Book("Head First Java", "Kathy Sierra and Bert Bates", 2005));
        repository.save(new Book("Java Performance: The Definitive Guide", "Scott Oaks", 2014));
        repository.save(new Book("Java Puzzlers: Traps, Pitfalls, and Corner Cases", "Joshua Bloch and Neal Gafter", 2005));
        repository.save(new Book("Java 8 in Action", "Raoul-Gabriel Urma, Mario Fusco, and Alan Mycroft", 2014));
        repository.save(new Book("Modern Java in Action", "Raoul-Gabriel Urma, Mario Fusco, and Alan Mycroft", 2018));
        repository.save(new Book("Java: A Beginner's Guide", "Herbert Schildt", 2018));
        repository.save(new Book("Spring 5 Design Patterns", "Dinesh Rajput", 2017));
        repository.save(new Book("Spring MVC: A Tutorial", "Paul Deck", 2014));
        repository.save(new Book("Spring Batch in Action", "Arnaud Cogoluegnes, Thierry Templier, Gary Gregory, and Olivier Bazoud", 2011));
        repository.save(new Book("Spring Integration in Action", "Mark Fisher, Jonas Partner, Marius Bogoevici, and Iwein Fuld", 2012));
        repository.save(new Book("Spring Data", "Mark Pollack, Oliver Gierke, Thomas Risberg, Jon Brisbin, and Michael Hunger", 2012));
        repository.save(new Book("Spring Recipes: A Problem-Solution Approach", "Gary Mak, Josh Long, and Daniel Rubio", 2010));
        repository.save(new Book("Spring Microservices", "Rajesh RV", 2016));
        repository.save(new Book("Spring Boot Cookbook", "Alex Antonov", 2015));
    }


}
