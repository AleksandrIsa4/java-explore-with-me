package ru.practicum.model.categorie;

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
@Table(name = "categories", schema = "public")
@SuperBuilder
public class Categorie extends BaseModel<Long> {

    @Column(name = "name")
    String name;
}
