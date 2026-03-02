package com.oceanview.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Redirects unauthenticated requests to /login.
 * Allows /login, /logout, static resources (css, js, images) through.
 */
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String ctx = req.getContextPath();

        // Allow login/logout and static resources without authentication
        String path = uri.substring(ctx.length());
        if (path.equals("/login") || path.equals("/logout")
                || path.startsWith("/css/") || path.startsWith("/js/")
                || path.startsWith("/images/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(ctx + "/login");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override public void init(FilterConfig config) {}
    @Override public void destroy() {}
}
