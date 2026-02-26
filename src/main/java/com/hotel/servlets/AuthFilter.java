package com.hotel.servlets;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * AuthFilter — Checks session on every protected request.
 * Implements session-based authentication (SESSIONS/COOKIES requirement).
 */
@WebFilter(urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    private static final String[] PUBLIC = {"/login", "/css/", "/js/", "/images/"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req = (HttpServletRequest)  request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getServletPath();

        // Allow public resources
        for (String pub : PUBLIC) {
            if (path.startsWith(pub)) { chain.doFilter(request, response); return; }
        }

        // Check session
        HttpSession session = req.getSession(false);
        boolean loggedIn    = session != null && session.getAttribute("loggedUser") != null;

        if (!loggedIn && !"/login".equals(path)) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Security headers
        res.setHeader("X-Content-Type-Options",  "nosniff");
        res.setHeader("X-Frame-Options",         "DENY");
        res.setHeader("X-XSS-Protection",        "1; mode=block");
        res.setHeader("Cache-Control",           "no-store");

        chain.doFilter(request, response);
    }

    @Override public void init(FilterConfig fc) {}
    @Override public void destroy() {}
}