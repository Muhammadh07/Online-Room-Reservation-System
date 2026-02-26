package com.hotel.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * RequestHandler — COMMAND PATTERN interface.
 * Each handler encapsulates a specific request-handling operation.
 * The servlet acts as the Invoker; handlers are the concrete Commands.
 */
public interface RequestHandler {
    /**
     * Handle the incoming request and return the view path to forward to,
     * or a redirect URL prefixed with "redirect:".
     */
    String handle(HttpServletRequest req, HttpServletResponse res) throws Exception;
}