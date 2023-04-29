package ru.practicum.model.enumeration;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
public enum Grade {

    LIKE(1L),
    DISLIKE(-1L);

    Long score;
}
