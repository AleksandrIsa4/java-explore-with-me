package ru.practicum.model.rating;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
@Table(name = "rating", schema = "public")
@SuperBuilder
public class Rating {

    @EmbeddedId
    RatingPK pk;


    @Column(name = "like_event")
    Long likeEvent;
}
