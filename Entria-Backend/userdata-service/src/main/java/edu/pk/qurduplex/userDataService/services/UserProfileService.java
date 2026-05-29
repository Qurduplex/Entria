package edu.pk.qurduplex.userDataService.services;

import edu.pk.qurduplex.userDataService.models.UserProfile;
import edu.pk.qurduplex.userDataService.repositories.UserProfileRepository;
import edu.pk.qurduplex.common.messages.user.CreateProfileMessage;
import org.springframework.stereotype.Service;
import edu.pk.qurduplex.userDataService.exceptions.UserProfileNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import edu.pk.qurduplex.userDataService.dto.*;
import java.util.UUID;
import java.time.LocalDate;
import java.time.DateTimeException;
import edu.pk.qurduplex.userDataService.exceptions.InvalidProfileDataException;

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

    public UserProfile getUserProfile(UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId).orElseThrow(() -> new UserProfileNotFoundException("User profile not found for user id: " + userId));
        return userProfile;
    }

    public UserProfile updateUserProfile(UUID userId, UpdateUserProfileRequestDTO request) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId).orElseThrow(() -> new UserProfileNotFoundException("User profile not found for user id: " + userId));
        if(request.getFirstName() != null)  userProfile.setFirstName(request.getFirstName());
        if(request.getLastName() != null) userProfile.setLastName(request.getLastName());
        if(request.getEmail() != null) userProfile.setEmail(request.getEmail());
        if(request.getPhoneNumber() != null) userProfile.setPhoneNumber(request.getPhoneNumber());
        if(request.getPesel() != null) userProfile.setPesel(request.getPesel());
        if(request.getSex() != null) userProfile.setSex(request.getSex());
        if(request.getBirthDate() != null) {
            try{
            userProfile.setBirthDate(LocalDate.parse(request.getBirthDate()));
            } catch (DateTimeException exc) {
                throw new InvalidProfileDataException("Invalid birth date format");
            }
        }
        if(request.getProfilePictureUrl() != null) userProfile.setProfilePictureUrl(request.getProfilePictureUrl());
        return userProfileRepository.save(userProfile);
    }
}
