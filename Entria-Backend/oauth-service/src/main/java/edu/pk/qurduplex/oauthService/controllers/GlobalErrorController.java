package edu.pk.qurduplex.oauthService.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class GlobalErrorController implements ErrorController {

    @GetMapping("/oauth2/error")
    public String showAuthorizationError(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String state,
            Model model) {

        log.warn("Authorization error occurred - error: {}, message: {}", error, message);

        model.addAttribute("error", error);
        model.addAttribute("message", message);
        model.addAttribute("state", state);

        return "error";
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        log.error("==== CRITICAL APPLICATION ERROR ====");
        log.error("HTTP Status: {}", status);
        log.error("Message: {}", message);

        if (exception instanceof Exception e) {
            log.error("Error stack trace:", e);
        } else if (exception != null) {
            log.error("Exception: {}", exception);
        }
        log.error("====================================");

        model.addAttribute("error", "unknown_error");

        if (message != null && !message.toString().isEmpty()) {
            model.addAttribute("message", message.toString());
        }

        return "error";
    }
}

