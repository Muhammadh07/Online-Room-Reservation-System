package com.oceanview.servlets;

import com.oceanview.db.DatabaseInitializer;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * load-on-startup=1 servlet — runs DatabaseInitializer before any request is handled.
 * Creates/upgrades all tables and seeds default data automatically.
 */
public class InitServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        DatabaseInitializer.initialize();
    }
}
