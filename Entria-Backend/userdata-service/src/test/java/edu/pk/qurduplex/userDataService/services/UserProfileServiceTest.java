package edu.pk.qurduplex.userDataService.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import edu.pk.qurduplex.userDataService.repositories.UserProfileRepository;
import java.util.UUID;
import edu.pk.qurduplex.userDataService.models.UserProfile;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import edu.pk.qurduplex.userDataService.dto.UpdateUserProfileRequestDTO;
import edu.pk.qurduplex.userDataService.exceptions.UserProfileNotFoundException;
import java.time.LocalDate;
import edu.pk.qurduplex.userDataService.exceptions.InvalidProfileDataException;
import edu.pk.qurduplex.common.messages.user.CreateProfileMessage;
import static org.mockito.ArgumentMatchers.argThat;
import edu.pk.qurduplex.userDataService.mappers.UserProfileMapper;

@ExtendWith(MockitoExtension.class)
public class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    @Test
    @DisplayName("Should successfully create a new user profile")
    void getUserProfile_found(){
        UUID userID = UUID.randomUUID();
        UserProfile userProfile = UserProfile.builder()
            .userId(userID)
            .firstName("John")
            .lastName("Doe")
            .phoneNumber("1234567890")
            .pesel("1234567890123")
            .sex("M")
            .build();

        when(userProfileRepository.findByUserId(userID)).thenReturn(Optional.of(userProfile));
        UserProfile result = userProfileService.getUserProfile(userID);
        assertThat(result.getFirstName()).isEqualTo(userProfile.getFirstName());
        verify(userProfileRepository).findByUserId(userID);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user profile is not found")
    void getUserProfile_notFound(){
        UUID userID = UUID.randomUUID();
        when(userProfileRepository.findByUserId(userID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userProfileService.getUserProfile(userID))
            .isInstanceOf(UserProfileNotFoundException.class)
            .hasMessageContaining("User profile not found for user id:");
        verify(userProfileRepository).findByUserId(userID);
    }

    @Test
    @DisplayName("Should successfully update user profile")
    void updateUserProfile_success(){
        UUID userID = UUID.randomUUID();
        UserProfile userProfile = UserProfile.builder()
            .userId(userID)
            .firstName("John")
            .lastName("Doe")

            .phoneNumber("1234567890")
            .pesel("1234567890123")
            .sex("M")
            .build();
        when(userProfileRepository.findByUserId(userID)).thenReturn(Optional.of(userProfile));
        UpdateUserProfileRequestDTO request = UpdateUserProfileRequestDTO.builder()
            .firstName("John")
            .lastName("Doe")
            .phoneNumber("1234567890")
            .pesel("1234567890123")
            .sex("M")
            .build();
        userProfileService.updateUserProfile(userID, UserProfileMapper.toUserProfileUpdates(request), request.getProfilePicture());
        verify(userProfileRepository).save(userProfile);
    }

    @Test
    @DisplayName("Should throw InvalidProfileDataException when birth date is invalid")
    void updateUserProfile_invalidData(){
        UUID userID = UUID.randomUUID();
        UserProfile userProfile = UserProfile.builder()
            .userId(userID)
            .birthDate(LocalDate.of(2000, 5, 5))
            .build();
        when(userProfileRepository.findByUserId(userID)).thenReturn(Optional.of(userProfile));
        UpdateUserProfileRequestDTO request = UpdateUserProfileRequestDTO.builder()
            .birthDate("05-05-2000")
            .build();
        assertThatThrownBy(() -> userProfileService.updateUserProfile(userID, UserProfileMapper.toUserProfileUpdates(request), request.getProfilePicture()))
            .isInstanceOf(InvalidProfileDataException.class)
            .hasMessageContaining("Invalid birth date");
        verify(userProfileRepository).findByUserId(userID);
    }

    @Test
    @DisplayName("Should successfully create a new user profile from registration event")
    void createFromRegistrationEvent(){
        UUID userID = UUID.randomUUID();
        String firstName = "John";
        String lastName = "Doe";
        CreateProfileMessage event = new CreateProfileMessage(userID, firstName, lastName);
        userProfileService.createFromRegistrationEvent(event);
        verify(userProfileRepository).save(argThat(userProfile -> userProfile.getUserId().equals(userID) && userProfile.getFirstName().equals(firstName) && userProfile.getLastName().equals(lastName)));

    }

}
