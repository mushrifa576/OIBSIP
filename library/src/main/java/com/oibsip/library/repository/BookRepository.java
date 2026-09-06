package com.oibsip.library.repository;

import com.oibsip.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByCategoryIgnoreCaseOrderByTitleAsc(String category);

    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrderByTitleAsc(
            String title, String author);

    Optional<Book> findByIsbn(String isbn);

    @Query("SELECT DISTINCT b.category FROM Book b ORDER BY b.category ASC")
    List<String> findDistinctCategories();

    List<Book> findAllByOrderByTitleAsc();
}
