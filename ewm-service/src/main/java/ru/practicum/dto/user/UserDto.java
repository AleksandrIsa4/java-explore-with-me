package ru.practicum.dto.user;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.abstraction.BaseModel;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@SuperBuilder
public class UserDto extends BaseModel<Long> {
    String name;
    String email;
}
