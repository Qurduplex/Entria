package edu.pk.qurduplex.userDataService.mappers;

import edu.pk.qurduplex.userDataService.models.UserProfile;
import edu.pk.qurduplex.userDataService.dto.UserProfileResponseDTO;
import edu.pk.qurduplex.userDataService.models.UserProfileUpdate;
import edu.pk.qurduplex.userDataService.dto.UpdateUserProfileRequestDTO;

public final class UserProfileMapper {
    
    public final static UserProfileResponseDTO toResponseDTO(UserProfile userProfile) {
        return UserProfileResponseDTO.builder()
                .userId(userProfile.getUserId())
                .firstName(userProfile.getFirstName())
                .lastName(userProfile.getLastName())
                .phoneNumber(userProfile.getPhoneNumber())
                .pesel(userProfile.getPesel())
                .sex(userProfile.getSex())
                .birthDate(userProfile.getBirthDate() != null ? userProfile.getBirthDate().toString() : null)
                .profilePictureUrl(userProfile.getProfilePictureUrl())
                .build();
    }

    public final static UserProfileUpdate toUserProfileUpdates(UpdateUserProfileRequestDTO update) {
        return UserProfileUpdate.builder()
                .firstName(update.getFirstName())
                .lastName(update.getLastName())
                .phoneNumber(update.getPhoneNumber())
                .pesel(update.getPesel())
                .sex(update.getSex())
                .birthDate(update.getBirthDate())
                .build();
    }

}
