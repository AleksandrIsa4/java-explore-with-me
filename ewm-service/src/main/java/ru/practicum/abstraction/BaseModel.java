package ru.practicum.abstraction;

import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
@SuperBuilder
public abstract class BaseModel<T> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    protected T id;

    public BaseModel() {
    }

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }
}
