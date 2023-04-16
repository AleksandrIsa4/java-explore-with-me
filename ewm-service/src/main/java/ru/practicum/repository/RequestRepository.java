package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.enumeration.Status;
import ru.practicum.model.request.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    Long countByEventIdAndStatus(Long eventId, Status status);

    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByRequesterIdAndEventId(Long userId,Long eventId);

    List<Request>  findByIdIn (List<Long> ids);
}
