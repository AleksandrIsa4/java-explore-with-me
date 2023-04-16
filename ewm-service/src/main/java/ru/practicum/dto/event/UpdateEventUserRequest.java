package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.model.categorie.Categorie;
import ru.practicum.model.enumeration.State;
import ru.practicum.model.enumeration.StateAction;
import ru.practicum.model.event.Location;
import ru.practicum.model.user.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@SuperBuilder
public class UpdateEventUserRequest {

    @Size(max = 2000, min = 20)
    String annotation;

    Long category;

    @Size(max = 7000, min = 20)
    String description;

    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    Location location;

    Boolean paid;

    Long participantLimit;

    Boolean requestModeration;

    StateAction stateAction;

    @NotNull
    @Size(max = 120, min = 3)
    String title;
}
