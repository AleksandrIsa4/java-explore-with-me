package ru.practicum.dto.compilation;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class UpdateCompilationRequest {

    List<Long> events;

    Boolean pinned;

    String title;
}
