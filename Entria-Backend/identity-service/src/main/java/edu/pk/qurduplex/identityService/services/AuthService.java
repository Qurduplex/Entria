package edu.pk.qurduplex.identityService.services;

import edu.pk.qurduplex.identityService.repositories.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final AuthRepository authRepository;


}
