package ru.practicum.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.abstraction.BaseModel;
import ru.practicum.model.categorie.Categorie;
import ru.practicum.model.user.User;
import ru.practicum.model.enumeration.State;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
@Table(name = "events", schema = "public")
@SuperBuilder
public class Event extends BaseModel<Long> {

    @Column(name = "annotation")
    String annotation;

    @ManyToOne()
    @JoinColumn(name = "category_id")
    Categorie category;

    @Column(name = "created_on")
    LocalDateTime createdOn;

    @Column(name = "description")
    String description;

    @Column(name = "event_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @ManyToOne()
    @JoinColumn(name = "initiator_id")
    User initiator;

    @Embedded
    Location location;

    @Column(name = "paid")
    Boolean paid;

    @Column(name = "participant_limit")
    Long participantLimit;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    State state;

    @Column(name = "title")
    String title;
}
