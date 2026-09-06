package com.oibsip.library.controller;

import com.oibsip.library.model.Book;
import com.oibsip.library.service.LibraryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {

    private final LibraryService libraryService;

    public UserController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    private Long currentUserId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        Long userId = currentUserId(session);
        model.addAttribute("fullName", session.getAttribute("fullName"));
        model.addAttribute("activeIssuesCount", libraryService.getActiveIssuesForUser(userId).size());
        model.addAttribute("reservationsCount", libraryService.getReservationsForUser(userId).size());
        return "user/dashboard";
    }

    // =====================================================================
    // Catalogue: browse, filter by category, search
    // =====================================================================

    @GetMapping("/catalogue")
    public String catalogue(@RequestParam(required = false) String category,
                             @RequestParam(required = false) String search,
                             Model model) {
        java.util.List<Book> books;
        if (search != null && !search.isBlank()) {
            books = libraryService.search(search);
        } else {
            books = libraryService.browseByCategory(category);
        }
        model.addAttribute("books", books);
        model.addAttribute("categories", libraryService.getAllCategories());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("searchTerm", search);
        return "user/catalogue";
    }

    @PostMapping("/issue/{bookId}")
    public String issue(@PathVariable Long bookId, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            libraryService.issueBook(currentUserId(session), bookId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Book issued! Check \"My Books\" for the due date.");
        } catch (LibraryService.IssueException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/user/catalogue";
    }

    @PostMapping("/reserve/{bookId}")
    public String reserve(@PathVariable Long bookId, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            libraryService.reserveBook(currentUserId(session), bookId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Reservation placed. Check \"My Reservations\" to see when it's available.");
        } catch (LibraryService.ReservationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/user/catalogue";
    }

    // =====================================================================
    // My Books: currently issued + history, and returning
    // =====================================================================

    @GetMapping("/mybooks")
    public String myBooks(Model model, HttpSession session) {
        Long userId = currentUserId(session);
        model.addAttribute("activeIssues", libraryService.getActiveIssuesForUser(userId));
        model.addAttribute("history", libraryService.getFullHistoryForUser(userId));
        return "user/mybooks";
    }

    @PostMapping("/return/{issueRecordId}")
    public String returnBook(@PathVariable Long issueRecordId, HttpSession session,
                              RedirectAttributes redirectAttributes) {
        try {
            double fine = libraryService.returnBook(issueRecordId, currentUserId(session));
            if (fine > 0) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        String.format("Book returned, but it was overdue. Fine charged: \u20b9%.2f", fine));
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "Book returned on time. Thank you!");
            }
        } catch (LibraryService.IssueException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/user/mybooks";
    }

    // =====================================================================
    // My Reservations
    // =====================================================================

    @GetMapping("/reservations")
    public String reservations(Model model, HttpSession session) {
        model.addAttribute("reservations", libraryService.getReservationsForUser(currentUserId(session)));
        return "user/reservations";
    }

    @PostMapping("/reservations/cancel/{id}")
    public String cancelReservation(@PathVariable Long id, HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        try {
            libraryService.cancelReservation(id, currentUserId(session));
            redirectAttributes.addFlashAttribute("successMessage", "Reservation cancelled.");
        } catch (LibraryService.ReservationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/user/reservations";
    }

    // =====================================================================
    // Contact / query form
    // =====================================================================

    @GetMapping("/contact")
    public String contactPage() {
        return "user/contact";
    }

    @PostMapping("/contact")
    public String submitContact(@RequestParam String name,
                                 @RequestParam String email,
                                 @RequestParam String message,
                                 RedirectAttributes redirectAttributes) {
        libraryService.submitContactMessage(name, email, message);
        redirectAttributes.addFlashAttribute("successMessage", "Your message has been sent to the library admin.");
        return "redirect:/user/contact";
    }
}
