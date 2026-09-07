package com.oibsip.library.config;

import com.oibsip.library.model.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Simple session-based access control (no Spring Security, to keep this
 * project approachable for a student submission):
 *   - "/admin/**"  requires a logged-in session with role == ADMIN
 *   - "/user/**"   requires any logged-in session (ADMIN or USER)
 * Anything else (login, register, static assets, home) is public.
 */
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                              @NonNull HttpServletResponse response,
                              @NonNull Object handler) throws Exception {
        String path = request.getRequestURI();
        HttpSession session = request.getSession(false);
        Object userId = session != null ? session.getAttribute("userId") : null;
        Object role = session != null ? session.getAttribute("role") : null;

        if (path.startsWith("/admin")) {
            if (userId == null || role != Role.ADMIN) {
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
            return true;
        }

        if (path.startsWith("/user")) {
            if (userId == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
            return true;
        }

        return true;
    }
}
