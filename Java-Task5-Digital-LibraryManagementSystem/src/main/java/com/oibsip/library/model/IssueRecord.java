package com.oibsip.library.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Represents one instance of a book being issued to a user. If
 * {@code returnDate} is null, the book is still out with that user.
 * Once returned, {@code fineAmount} holds whatever overdue fine (if any)
 * was calculated at return time, and {@code finePaid} tracks whether the
 * admin has since marked it settled.
 */
@Entity
@Table(name = "issue_record")
public class IssueRecord {

    /** Flat rate charged per day overdue. */
    public static final double FINE_PER_DAY = 5.0;

    /** How many days a book may be kept before it's considered overdue. */
    public static final int LOAN_PERIOD_DAYS = 14;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    /** Null while the book is still checked out. */
    private LocalDate returnDate;

    @Column(nullable = false)
    private double fineAmount = 0.0;

    @Column(nullable = false)
    private boolean finePaid = false;

    public IssueRecord() {
    }

    public IssueRecord(Book book, User user, LocalDate issueDate, LocalDate dueDate) {
        this.book = book;
        this.user = user;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public boolean isFinePaid() {
        return finePaid;
    }

    public void setFinePaid(boolean finePaid) {
        this.finePaid = finePaid;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public boolean isOverdue() {
        return !isReturned() && LocalDate.now().isAfter(dueDate);
    }
}
