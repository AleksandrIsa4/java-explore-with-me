package ru.practicum.model.enumeration;

import lombok.*;

@AllArgsConstructor
@Getter
@ToString
public enum Grade {

    LIKE(1L),
    DISLIKE(-1L);

    private final Long score;
}
