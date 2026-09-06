package com.oibsip.library.repository;

import com.oibsip.library.model.IssueRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IssueRecordRepository extends JpaRepository<IssueRecord, Long> {

    /** All books currently out on loan (admin's "Issued Books" view). */
    List<IssueRecord> findByReturnDateIsNullOrderByDueDateAsc();

    /** A specific user's currently-issued books. */
    List<IssueRecord> findByUser_IdAndReturnDateIsNullOrderByDueDateAsc(Long userId);

    /** A specific user's full history (issued + returned), most recent first. */
    List<IssueRecord> findByUser_IdOrderByIssueDateDesc(Long userId);

    /** For the ownership check when a user tries to return a book. */
    Optional<IssueRecord> findByIdAndUser_Id(Long id, Long userId);

    /** For the "you already have this book" duplicate-issue guard. */
    boolean existsByBook_IdAndUser_IdAndReturnDateIsNull(Long bookId, Long userId);

    /** Admin's fine management view - every record that ever incurred a fine. */
    List<IssueRecord> findByFineAmountGreaterThanOrderByFinePaidAscIssueDateDesc(double amount);

    /** Used by the delete-book guard to block deleting a book on active loan. */
    long countByBook_IdAndReturnDateIsNull(Long bookId);
}
