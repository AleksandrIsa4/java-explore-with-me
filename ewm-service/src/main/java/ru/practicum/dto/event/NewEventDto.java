package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.event.Location;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class NewEventDto {

    @NotBlank
    @NotNull
    @Size(max = 2000, min = 20)
    String annotation;

    @NotNull
    Long category;

    @NotNull
    @Size(max = 7000, min = 20)
    String description;

    @NotNull
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @NotNull
    Location location;

    Boolean paid;

    Long participantLimit;

    Boolean requestModeration;

    @NotNull
    @Size(max = 120, min = 3)
    String title;
}
