package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.rating.Rating;
import ru.practicum.model.rating.RatingPK;

public interface RatingRepository extends JpaRepository<Rating, RatingPK> {

    @Query("SELECT CASE WHEN SUM(r.likeEvent) IS NULL THEN 0L ELSE SUM(r.likeEvent) END " +
            "FROM Rating r " +
            "WHERE (r.pk.event.id=:eventId) ")
    Long sumLike(Long eventId);
}