package com.hotel.handlers;

import com.hotel.model.User;
import com.hotel.services.AuthService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * LoginHandler — COMMAND PATTERN concrete command.
 * Handles authentication logic for POST /login.
 */
public class LoginHandler implements RequestHandler {

    private final AuthService authService = AuthService.getInstance();

    @Override
    public String handle(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        HttpSession session = req.getSession(true);

        // Track failed attempts
        Integer attempts = (Integer) session.getAttribute("loginAttempts");
        if (attempts == null) attempts = 0;

        if (attempts >= AuthService.MAX_ATTEMPTS) {
            req.setAttribute("error", "Account locked. Too many failed attempts. Please contact admin.");
            req.setAttribute("locked", true);
            return "/jsp/common/login.jsp";
        }

        User user = authService.authenticate(username, password);
        if (user == null) {
            attempts++;
            session.setAttribute("loginAttempts", attempts);
            int remaining = AuthService.MAX_ATTEMPTS - attempts;
            req.setAttribute("error", "Invalid credentials. " +
                    (remaining > 0 ? remaining + " attempt(s) remaining." : "Account locked."));
            req.setAttribute("username", username);
            return "/jsp/common/login.jsp";
        }

        // Success — set session attributes
        session.setAttribute("loggedUser", user);
        session.setAttribute("userId",    user.getUserId());
        session.setAttribute("userRole",  user.getRole());
        session.setAttribute("userName",  user.getFullName());
        session.removeAttribute("loginAttempts");
        session.setMaxInactiveInterval(30 * 60); // 30 minutes

        return user.isAdmin()
                ? "redirect:dashboard"
                : "redirect:dashboard";
    }
}