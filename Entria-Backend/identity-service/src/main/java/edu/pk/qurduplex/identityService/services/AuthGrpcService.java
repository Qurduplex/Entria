package edu.pk.qurduplex.identityService.services;

import edu.pk.qurduplex.common.grpc.*;
import edu.pk.qurduplex.identityService.models.AuthCredential;
import edu.pk.qurduplex.identityService.repositories.AuthRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class AuthGrpcService extends AuthServiceGrpc.AuthServiceImplBase {

    private final JwtService jwtService;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void validateToken(ValidateTokenRequest request, StreamObserver<ValidateTokenResponse> responseObserver) {
        try {
            String token = request.getToken();


            String userId = jwtService.extractUserId(token);
            String role = jwtService.extractUserRole(token).name();

            ValidateTokenResponse response = ValidateTokenResponse.newBuilder()
                    .setUserId(userId)
                    .setRole(role)
                    .setIsValid(true)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.warn("gRPC Token validation failed: {}", e.getMessage());
            ValidateTokenResponse response = ValidateTokenResponse.newBuilder()
                    .setIsValid(false)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void verifyCredentials(VerifyCredentialsRequest request, StreamObserver<VerifyCredentialsResponse> responseObserver) {
        try {
            Optional<AuthCredential> authOpt = authRepository.findByEmail(request.getEmail());

            if (authOpt.isPresent()) {
                AuthCredential auth = authOpt.get();

                if (passwordEncoder.matches(request.getRawPassword(), auth.getPasswordHash())) {

                    VerifyCredentialsResponse response = VerifyCredentialsResponse.newBuilder()
                            .setIsCorrect(true)
                            .setUserId(auth.getId().toString())
                            .setRole(auth.getRole().name())
                            .build();

                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                    return;
                }
            }

            VerifyCredentialsResponse response = VerifyCredentialsResponse.newBuilder()
                    .setIsCorrect(false)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error while gRPC verifyCredentials: ", e);
            responseObserver.onNext(VerifyCredentialsResponse.newBuilder().setIsCorrect(false).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getUserEmail(GetUserEmailRequest request, StreamObserver<GetUserEmailResponse> responseObserver) {
        try {
            authRepository.findById(UUID.fromString(request.getUserId()))
                    .ifPresentOrElse(authOpt -> {
                        responseObserver.onNext(GetUserEmailResponse.newBuilder()
                                .setEmail(authOpt.getEmail())
                                .build());
                    }, () -> responseObserver.onNext(GetUserEmailResponse.newBuilder().build()));

            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC GetUserEmail error: ", e);
            responseObserver.onNext(GetUserEmailResponse.newBuilder().build());
            responseObserver.onCompleted();
        }
    }
}
