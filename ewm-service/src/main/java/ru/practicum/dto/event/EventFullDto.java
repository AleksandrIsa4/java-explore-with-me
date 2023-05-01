package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.abstraction.BaseModel;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.categorie.Category;
import ru.practicum.model.event.Location;
import ru.practicum.model.enumeration.State;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@SuperBuilder
public class EventFullDto extends BaseModel<Long> {

    String annotation;

    Category category;

    Long confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    LocalDateTime createdOn;

    String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    LocalDateTime eventDate;

    UserShortDto initiator;

    Location location;

    Boolean paid;

    Long participantLimit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    LocalDateTime publishedOn;

    Boolean requestModeration;

    State state;

    String title;

    Long views;

    Long rating;
}
