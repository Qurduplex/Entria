package edu.pk.qurduplex.oauthService.security;

import edu.pk.qurduplex.common.grpc.AuthServiceGrpc;
import edu.pk.qurduplex.common.grpc.VerifyCredentialsRequest;
import edu.pk.qurduplex.common.grpc.VerifyCredentialsResponse;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GrpcAuthenticationProvider implements AuthenticationProvider {

    @GrpcClient("identity-service")
    private AuthServiceGrpc.AuthServiceBlockingStub authStub;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        VerifyCredentialsResponse response = authStub.verifyCredentials(
                VerifyCredentialsRequest.newBuilder()
                        .setEmail(email)
                        .setRawPassword(password)
                        .build()
        );

        if (response.getIsCorrect()) {
            if (!response.getIsActive()) {
                throw new DisabledException("Account is unverified.");
            }

            if (!"USER".equalsIgnoreCase(response.getRole())) {
                throw new BadCredentialsException("Access denied: only users with role 'USER' are allowed to authenticate.");
            }

            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + response.getRole()));
            return new UsernamePasswordAuthenticationToken(response.getUserId(), null, authorities);
        } else {
            throw new BadCredentialsException("Invalid email address or password");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
