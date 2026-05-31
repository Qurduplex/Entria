package edu.pk.qurduplex.identityService.services;

import edu.pk.qurduplex.common.grpc.AuthServiceGrpc;
import edu.pk.qurduplex.common.grpc.ValidateTokenRequest;
import edu.pk.qurduplex.common.grpc.ValidateTokenResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class AuthGrpcService extends AuthServiceGrpc.AuthServiceImplBase {

    private final JwtService jwtService;

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
}
