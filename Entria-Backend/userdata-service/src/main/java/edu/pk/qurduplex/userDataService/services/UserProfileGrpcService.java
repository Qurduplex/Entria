package edu.pk.qurduplex.userDataService.services;

import edu.pk.qurduplex.common.grpc.UserProfileGrpcServiceGrpc;
import edu.pk.qurduplex.common.grpc.UserProfileRequest;
import edu.pk.qurduplex.common.grpc.UserProfileResponse;
import edu.pk.qurduplex.userDataService.repositories.UserProfileRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserProfileGrpcService extends UserProfileGrpcServiceGrpc.UserProfileGrpcServiceImplBase {

    private final UserProfileRepository userProfileRepository;

    @Override
    public void getUserProfile(UserProfileRequest request, StreamObserver<UserProfileResponse> responseObserver) {
        String incomingUserId = request.getUserId();
        log.info("[gRPC userdata-service] Received getUserProfile request for userId: '{}'", incomingUserId);

        try {
            UUID userUuid = UUID.fromString(incomingUserId);

            userProfileRepository.findById(userUuid)
                    .ifPresentOrElse(profile -> {
                        log.info("[gRPC userdata-service] Found profile in DB for user: {}. FirstName: '{}', LastName: '{}'",
                                userUuid, profile.getFirstName(), profile.getLastName());

                        UserProfileResponse.Builder builder = UserProfileResponse.newBuilder()
                                .setFirstName(profile.getFirstName() != null ? profile.getFirstName() : "")
                                .setLastName(profile.getLastName() != null ? profile.getLastName() : "")
                                .setPhoneNumber(profile.getPhoneNumber() != null ? profile.getPhoneNumber() : "")
                                .setPesel(profile.getPesel() != null ? profile.getPesel() : "")
                                .setSex(profile.getSex() != null ? profile.getSex() : "")
                                .setProfilePictureUrl(profile.getProfilePictureUrl() != null ? profile.getProfilePictureUrl() : "");

                        if (profile.getBirthDate() != null) {
                            builder.setBirthDate(profile.getBirthDate().toString());
                        }

                        UserProfileResponse response = builder.build();
                        log.debug("[gRPC userdata-service] Returning response: {}", response);
                        responseObserver.onNext(response);

                    }, () -> {
                        log.warn("[gRPC userdata-service] Profile NOT FOUND in DB for user: {}", userUuid);
                        responseObserver.onNext(UserProfileResponse.newBuilder().build());
                    });

            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            log.error("[gRPC userdata-service] Invalid UUID format received: '{}'", incomingUserId, e);
            responseObserver.onNext(UserProfileResponse.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC userdata-service] Unexpected error processing GetUserProfile: ", e);
            responseObserver.onNext(UserProfileResponse.newBuilder().build());
            responseObserver.onCompleted();
        }
    }
}