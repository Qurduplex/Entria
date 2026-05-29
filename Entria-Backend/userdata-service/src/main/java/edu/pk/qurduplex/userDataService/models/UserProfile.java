package edu.pk.qurduplex.userDataService.models;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    @Id
    private UUID userId;

    // Wymagane podczas rejestracji
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String email;

    // Opcjonalne / dodatkowe infomracje uzytkownika
    private String phoneNumber;
    private String pesel;
    private String sex;
    private LocalDate birthDate;
    private String profilePictureUrl;
}
