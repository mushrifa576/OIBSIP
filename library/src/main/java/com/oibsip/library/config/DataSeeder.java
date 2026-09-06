package com.oibsip.library.config;

import com.oibsip.library.model.Book;
import com.oibsip.library.model.Role;
import com.oibsip.library.model.User;
import com.oibsip.library.repository.BookRepository;
import com.oibsip.library.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds a demo admin account, a demo user account, and a small book
 * catalogue the first time the app runs against an empty database, so
 * there's something to test with immediately.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public DataSeeder(UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedBooks();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) {
            return;
        }
        userRepository.save(new User("admin", "admin123", "Library Admin", "admin@library.local", Role.ADMIN));
        userRepository.save(new User("student1", "pass123", "Mushrifa", "mushrifa@library.local", Role.USER));
    }

    private void seedBooks() {
        if (bookRepository.count() > 0) {
            return;
        }
        bookRepository.save(new Book("Clean Code", "Robert C. Martin", "9780132350884", "Technology", 3));
        bookRepository.save(new Book("Effective Java", "Joshua Bloch", "9780134685991", "Technology", 2));
        bookRepository.save(new Book("A Brief History of Time", "Stephen Hawking", "9780553380163", "Science", 2));
        bookRepository.save(new Book("Cosmos", "Carl Sagan", "9780345539434", "Science", 1));
        bookRepository.save(new Book("Sapiens", "Yuval Noah Harari", "9780062316097", "History", 2));
        bookRepository.save(new Book("The Diary of a Young Girl", "Anne Frank", "9780553577129", "History", 1));
        bookRepository.save(new Book("1984", "George Orwell", "9780451524935", "Fiction", 2));
        bookRepository.save(new Book("To Kill a Mockingbird", "Harper Lee", "9780061120084", "Fiction", 1));
        bookRepository.save(new Book("The Hobbit", "J.R.R. Tolkien", "9780547928227", "Fiction", 2));
        bookRepository.save(new Book("Atomic Habits", "James Clear", "9780735211292", "Self-Help", 2));
    }
}
