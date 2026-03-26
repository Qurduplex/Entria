package edu.pk.qurduplex.identityService.controllers;

import edu.pk.qurduplex.identityService.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class AuthController {
    private final AuthService authService;


}
