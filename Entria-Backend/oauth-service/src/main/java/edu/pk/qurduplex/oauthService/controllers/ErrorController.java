package edu.pk.qurduplex.oauthService.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class ErrorController {

    /**
     * Wyświetla stronę błędu dla odrzuconych autoryzyacji
     */
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

    @GetMapping("/error")
    public String handleError(Model model) {
        log.error("General application error occurred");
        model.addAttribute("error", "unknown_error");
        return "error";
    }
}

