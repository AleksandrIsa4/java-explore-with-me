package ru.practicum.model.user;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.abstraction.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
@Table(name = "users", schema = "public")
@SuperBuilder
public class User extends BaseModel<Long> {

    @Column(name = "name")
    String name;

    @Column(name = "email", unique = true)
    String email;
}
