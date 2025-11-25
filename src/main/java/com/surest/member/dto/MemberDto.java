package com.surest.member.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

    private UUID id;

    @NotBlank(message = "First name must not be blank")
    @Size(max = 50, message = "First name must be at most 50 characters")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    @Size(max = 50, message = "Last name must be at most 50 characters")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be a past date")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email should be valid")
    private String email;

    // Usually set by system, so no validation on input
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // If version used for optimistic locking, should be non-negative
    @Min(value = 0, message = "Version must be zero or positive")
    private Long version;
}
