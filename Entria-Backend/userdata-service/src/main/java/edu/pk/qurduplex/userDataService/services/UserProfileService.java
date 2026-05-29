package edu.pk.qurduplex.userDataService.services;

import edu.pk.qurduplex.userDataService.models.UserProfile;
import edu.pk.qurduplex.userDataService.repositories.UserProfileRepository;
import edu.pk.qurduplex.common.messages.user.CreateProfileMessage;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserProfileService {
    
    private final UserProfileRepository userProfileRepository;

    public UserProfile createFromRegistrationEvent(CreateProfileMessage message) {
        if (userProfileRepository.existsByUserId(message.getUserId())) {
            log.warn("User profile already exists for user id: {}", message.getUserId());
            return userProfileRepository.findByUserId(message.getUserId()).orElse(null);
        }

        UserProfile userProfile = UserProfile.builder()
            .userId(message.getUserId())
            .firstName(message.getFirstName())
            .lastName(message.getLastName())
            .build();

        log.info("Creating new user profile for user id: {}", message.getUserId());
        UserProfile savedUserProfile = userProfileRepository.save(userProfile);
        log.info("User profile created successfully for user id: {}", message.getUserId());
        return savedUserProfile;
    }
}
