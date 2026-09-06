package com.oibsip.library.controller;

import com.oibsip.library.model.Book;
import com.oibsip.library.service.LibraryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final LibraryService libraryService;

    public AdminController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        model.addAttribute("fullName", session.getAttribute("fullName"));
        model.addAttribute("totalBooks", libraryService.countBooks());
        model.addAttribute("activeIssues", libraryService.countActiveIssues());
        model.addAttribute("totalMembers", libraryService.countMembers());
        model.addAttribute("unpaidFines", libraryService.totalUnpaidFines());
        return "admin/dashboard";
    }

    // =====================================================================
    // Book catalogue management (add / edit / delete)
    // =====================================================================

    @GetMapping("/books")
    public String books(@RequestParam(required = false) Long editId, Model model) {
        model.addAttribute("books", libraryService.getAllBooks());
        if (editId != null) {
            model.addAttribute("editingBook", libraryService.getBookById(editId));
        }
        return "admin/books";
    }

    @PostMapping("/books/add")
    public String addBook(@RequestParam String title,
                           @RequestParam String author,
                           @RequestParam String isbn,
                           @RequestParam String category,
                           @RequestParam int quantity,
                           RedirectAttributes redirectAttributes) {
        try {
            libraryService.addBook(title, author, isbn, category, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Book added: " + title);
        } catch (LibraryService.BookOperationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/books";
    }

    @PostMapping("/books/update/{id}")
    public String updateBook(@PathVariable Long id,
                              @RequestParam String title,
                              @RequestParam String author,
                              @RequestParam String isbn,
                              @RequestParam String category,
                              @RequestParam int quantity,
                              RedirectAttributes redirectAttributes) {
        try {
            libraryService.updateBook(id, title, author, isbn, category, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Book updated: " + title);
        } catch (LibraryService.BookOperationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/books";
    }

    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Book book = libraryService.getBookById(id);
            libraryService.deleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book deleted: " + book.getTitle());
        } catch (LibraryService.BookOperationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/books";
    }

    // =====================================================================
    // Issued books overview
    // =====================================================================

    @GetMapping("/issued")
    public String issuedBooks(Model model) {
        model.addAttribute("issues", libraryService.getAllActiveIssues());
        return "admin/issued";
    }

    // =====================================================================
    // Member accounts
    // =====================================================================

    @GetMapping("/members")
    public String members(Model model) {
        model.addAttribute("members", libraryService.getAllMembers());
        return "admin/members";
    }

    // =====================================================================
    // Fine management
    // =====================================================================

    @GetMapping("/fines")
    public String fines(Model model) {
        model.addAttribute("fines", libraryService.getAllFinedRecords());
        return "admin/fines";
    }

    @PostMapping("/fines/pay/{issueRecordId}")
    public String payFine(@PathVariable Long issueRecordId, RedirectAttributes redirectAttributes) {
        libraryService.markFinePaid(issueRecordId);
        redirectAttributes.addFlashAttribute("successMessage", "Fine marked as paid.");
        return "redirect:/admin/fines";
    }

    // =====================================================================
    // Contact / query messages
    // =====================================================================

    @GetMapping("/contacts")
    public String contacts(Model model) {
        model.addAttribute("messages", libraryService.getAllContactMessages());
        return "admin/contacts";
    }
}
