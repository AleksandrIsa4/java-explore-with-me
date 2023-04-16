package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.enumeration.State;
import ru.practicum.model.event.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Event findByInitiatorIdAndId(Long initiatorId, Long eventId);

    List<Event> findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateIsBeforeAndEventDateIsAfter(List<Long> users, List<State> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("select e " +
            "from Event e " +
            "where (lower(e.annotation) LIKE concat('%', ?1, '%') OR lower(e.description) LIKE concat('%', ?1, '%')) " +
            "AND (e.category IN ?2 OR ?2 IS NULL) " +
            "AND (e.paid=?3 OR ?3 IS NULL) " +
            "AND (e.eventDate BETWEEN ?4 AND ?5) " +
            "ORDER BY e.eventDate")
   //        (lower(e.annotation) LIKE concat('%', :text, '%') OR lower(e.description) LIKE concat('%', :text, '%'))
    List<Event> SearchEventPub (String text,List<Long> categories,Boolean paid,LocalDateTime rangeStart, LocalDateTime rangeEnd,Pageable pageable);
}
