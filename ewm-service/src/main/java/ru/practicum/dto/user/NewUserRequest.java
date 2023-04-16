package ru.practicum.dto.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class NewUserRequest {

    @NotBlank
    @NotNull
    String name;
    @NotBlank
    @Email
    @NotNull
    String email;
}
