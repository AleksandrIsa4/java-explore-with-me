package ru.practicum.dto.compilation;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class NewCompilationDto {

    List<Long> events;

    Boolean pinned;

    @NotBlank
    @NotNull
    String title;
}
