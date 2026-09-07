package com.oibsip.library.controller;

import com.oibsip.library.model.Role;
import com.oibsip.library.model.User;
import com.oibsip.library.service.LibraryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final LibraryService libraryService;

    public AuthController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    // ---- Home: send the visitor wherever makes sense for their session ----
    @GetMapping("/")
    public String home(HttpSession session) {
        Object role = session.getAttribute("role");
        if (role == Role.ADMIN) {
            return "redirect:/admin/dashboard";
        } else if (role == Role.USER) {
            return "redirect:/user/dashboard";
        }
        return "redirect:/login";
    }

    // ---- Login ----
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                         @RequestParam String password,
                         HttpServletRequest request,
                         Model model) {
        Optional<User> match = libraryService.authenticate(username, password);
        if (match.isEmpty()) {
            model.addAttribute("errorMessage", "Invalid username or password.");
            return "login";
        }

        User user = match.get();
        HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("fullName", user.getFullName());
        session.setAttribute("role", user.getRole());

        return user.getRole() == Role.ADMIN ? "redirect:/admin/dashboard" : "redirect:/user/dashboard";
    }

    // ---- Registration (new library members only - admins are seeded) ----
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                            @RequestParam String password,
                            @RequestParam String fullName,
                            @RequestParam String email,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        try {
            libraryService.register(username, password, fullName, email);
            redirectAttributes.addFlashAttribute("successMessage", "Account created! You can now log in.");
            return "redirect:/login";
        } catch (LibraryService.RegistrationException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("fullName", fullName);
            model.addAttribute("email", email);
            return "register";
        }
    }

    // ---- Logout ----
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }
}
