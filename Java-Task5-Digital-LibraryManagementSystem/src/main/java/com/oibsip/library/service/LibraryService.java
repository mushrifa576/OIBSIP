package com.oibsip.library.service;

import com.oibsip.library.model.*;
import com.oibsip.library.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * LibraryService
 * --------------
 * Holds every piece of business logic for the app in one place, so
 * controllers stay thin (just handling HTTP in/out) and all the actual
 * rules - "can this book be deleted?", "how is a fine calculated?",
 * "can this book be reserved?" - live in exactly one spot each.
 */
@Service
public class LibraryService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final IssueRecordRepository issueRecordRepository;
    private final ReservationRepository reservationRepository;
    private final ContactMessageRepository contactMessageRepository;

    public LibraryService(UserRepository userRepository,
                           BookRepository bookRepository,
                           IssueRecordRepository issueRecordRepository,
                           ReservationRepository reservationRepository,
                           ContactMessageRepository contactMessageRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.issueRecordRepository = issueRecordRepository;
        this.reservationRepository = reservationRepository;
        this.contactMessageRepository = contactMessageRepository;
    }

    // =====================================================================
    // Auth / users
    // =====================================================================

    public static class RegistrationException extends RuntimeException {
        public RegistrationException(String message) {
            super(message);
        }
    }

    public User register(String username, String password, String fullName, String email) {
        if (username == null || username.isBlank() || password == null || password.isBlank()
                || fullName == null || fullName.isBlank() || email == null || email.isBlank()) {
            throw new RegistrationException("All fields are required.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new RegistrationException("That username is already taken.");
        }
        User user = new User(username.trim(), password, fullName.trim(), email.trim(), Role.USER);
        return userRepository.save(user);
    }

    public Optional<User> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    public List<User> getAllMembers() {
        return userRepository.findByRole(Role.USER);
    }

    // =====================================================================
    // Books - admin CRUD
    // =====================================================================

    public static class BookOperationException extends RuntimeException {
        public BookOperationException(String message) {
            super(message);
        }
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAllByOrderByTitleAsc();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
    }

    public Book addBook(String title, String author, String isbn, String category, int quantity) {
        if (title.isBlank() || author.isBlank() || isbn.isBlank() || category.isBlank()) {
            throw new BookOperationException("All fields are required.");
        }
        if (quantity < 1) {
            throw new BookOperationException("Quantity must be at least 1.");
        }
        if (bookRepository.findByIsbn(isbn.trim()).isPresent()) {
            throw new BookOperationException("A book with that ISBN already exists.");
        }
        Book book = new Book(title.trim(), author.trim(), isbn.trim(), category.trim(), quantity);
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, String title, String author, String isbn, String category, int newTotalQuantity) {
        Book book = getBookById(id);
        if (title.isBlank() || author.isBlank() || isbn.isBlank() || category.isBlank()) {
            throw new BookOperationException("All fields are required.");
        }
        if (newTotalQuantity < 1) {
            throw new BookOperationException("Quantity must be at least 1.");
        }

        // If the ISBN changed, make sure it doesn't collide with a different book.
        Optional<Book> existingWithIsbn = bookRepository.findByIsbn(isbn.trim());
        if (existingWithIsbn.isPresent() && !existingWithIsbn.get().getId().equals(id)) {
            throw new BookOperationException("Another book already uses that ISBN.");
        }

        int delta = newTotalQuantity - book.getTotalQuantity();
        int newAvailable = Math.max(0, book.getAvailableQuantity() + delta);

        book.setTitle(title.trim());
        book.setAuthor(author.trim());
        book.setIsbn(isbn.trim());
        book.setCategory(category.trim());
        book.setTotalQuantity(newTotalQuantity);
        book.setAvailableQuantity(newAvailable);
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        Book book = getBookById(id);
        long activeLoans = issueRecordRepository.countByBook_IdAndReturnDateIsNull(id);
        if (activeLoans > 0) {
            throw new BookOperationException(
                    "Cannot delete \"" + book.getTitle() + "\" - it currently has " + activeLoans
                            + " copy(ies) issued out. Wait for all copies to be returned first.");
        }
        bookRepository.deleteById(id);
    }

    // =====================================================================
    // Books - browsing / search (user-facing)
    // =====================================================================

    public List<Book> browseByCategory(String category) {
        if (category == null || category.isBlank()) {
            return getAllBooks();
        }
        return bookRepository.findByCategoryIgnoreCaseOrderByTitleAsc(category.trim());
    }

    public List<Book> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllBooks();
        }
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrderByTitleAsc(
                keyword.trim(), keyword.trim());
    }

    public List<String> getAllCategories() {
        return bookRepository.findDistinctCategories();
    }

    // =====================================================================
    // Issuing / returning
    // =====================================================================

    public static class IssueException extends RuntimeException {
        public IssueException(String message) {
            super(message);
        }
    }

    public IssueRecord issueBook(Long userId, Long bookId) {
        Book book = getBookById(bookId);
        User user = getUserById(userId);

        if (book.getAvailableQuantity() <= 0) {
            throw new IssueException("No copies of \"" + book.getTitle() + "\" are currently available. "
                    + "You can place an advance booking instead.");
        }
        if (issueRecordRepository.existsByBook_IdAndUser_IdAndReturnDateIsNull(bookId, userId)) {
            throw new IssueException("You already have a copy of \"" + book.getTitle() + "\" issued.");
        }

        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        bookRepository.save(book);

        LocalDate today = LocalDate.now();
        IssueRecord record = new IssueRecord(book, user, today, today.plusDays(IssueRecord.LOAN_PERIOD_DAYS));
        return issueRecordRepository.save(record);
    }

    /**
     * Returns a book and calculates any overdue fine.
     * @return the fine amount charged (0.0 if returned on time).
     */
    public double returnBook(Long issueRecordId, Long userId) {
        IssueRecord record = issueRecordRepository.findByIdAndUser_Id(issueRecordId, userId)
                .orElseThrow(() -> new IssueException("That issued book was not found on your account."));

        if (record.isReturned()) {
            throw new IssueException("This book has already been returned.");
        }

        LocalDate today = LocalDate.now();
        record.setReturnDate(today);

        long daysLate = ChronoUnit.DAYS.between(record.getDueDate(), today);
        double fine = daysLate > 0 ? daysLate * IssueRecord.FINE_PER_DAY : 0.0;
        record.setFineAmount(fine);
        issueRecordRepository.save(record);

        Book book = record.getBook();
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        bookRepository.save(book);

        return fine;
    }

    public List<IssueRecord> getAllActiveIssues() {
        return issueRecordRepository.findByReturnDateIsNullOrderByDueDateAsc();
    }

    public List<IssueRecord> getActiveIssuesForUser(Long userId) {
        return issueRecordRepository.findByUser_IdAndReturnDateIsNullOrderByDueDateAsc(userId);
    }

    public List<IssueRecord> getFullHistoryForUser(Long userId) {
        return issueRecordRepository.findByUser_IdOrderByIssueDateDesc(userId);
    }

    // =====================================================================
    // Fines (admin)
    // =====================================================================

    public List<IssueRecord> getAllFinedRecords() {
        return issueRecordRepository.findByFineAmountGreaterThanOrderByFinePaidAscIssueDateDesc(0.0);
    }

    public void markFinePaid(Long issueRecordId) {
        IssueRecord record = issueRecordRepository.findById(issueRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Issue record not found: " + issueRecordId));
        record.setFinePaid(true);
        issueRecordRepository.save(record);
    }

    // =====================================================================
    // Reservations (advance booking)
    // =====================================================================

    public static class ReservationException extends RuntimeException {
        public ReservationException(String message) {
            super(message);
        }
    }

    public Reservation reserveBook(Long userId, Long bookId) {
        Book book = getBookById(bookId);
        User user = getUserById(userId);

        if (book.getAvailableQuantity() > 0) {
            throw new ReservationException(
                    "\"" + book.getTitle() + "\" is currently available - no need to reserve it, just issue it directly.");
        }
        if (reservationRepository.existsByBook_IdAndUser_IdAndStatus(bookId, userId, ReservationStatus.PENDING)) {
            throw new ReservationException("You already have a pending reservation for \"" + book.getTitle() + "\".");
        }

        Reservation reservation = new Reservation(book, user, LocalDate.now());
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsForUser(Long userId) {
        return reservationRepository.findByUser_IdOrderByReservationDateDesc(userId);
    }

    public void cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findByIdAndUser_Id(reservationId, userId)
                .orElseThrow(() -> new ReservationException("Reservation not found."));
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    // =====================================================================
    // Contact / query form
    // =====================================================================

    public ContactMessage submitContactMessage(String name, String email, String message) {
        if (name.isBlank() || email.isBlank() || message.isBlank()) {
            throw new IllegalArgumentException("All fields are required.");
        }
        ContactMessage cm = new ContactMessage(name.trim(), email.trim(), message.trim(), LocalDateTime.now());
        return contactMessageRepository.save(cm);
    }

    public List<ContactMessage> getAllContactMessages() {
        return contactMessageRepository.findAllByOrderBySubmittedAtDesc();
    }

    // =====================================================================
    // Dashboard stats (admin)
    // =====================================================================

    public long countBooks() {
        return bookRepository.count();
    }

    public long countActiveIssues() {
        return issueRecordRepository.findByReturnDateIsNullOrderByDueDateAsc().size();
    }

    public long countMembers() {
        return userRepository.findByRole(Role.USER).size();
    }

    public double totalUnpaidFines() {
        return issueRecordRepository.findByFineAmountGreaterThanOrderByFinePaidAscIssueDateDesc(0.0)
                .stream()
                .filter(r -> !r.isFinePaid())
                .mapToDouble(IssueRecord::getFineAmount)
                .sum();
    }
}
