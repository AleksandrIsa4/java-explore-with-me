package ru.practicum.service.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.DataNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.ViewStatsDto;
import ru.practicum.model.enumeration.Status;
import ru.practicum.model.event.Event;
import ru.practicum.model.rating.Rating;
import ru.practicum.model.rating.RatingPK;
import ru.practicum.model.request.Request;
import ru.practicum.model.user.User;
import ru.practicum.repository.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingPrivateService {

    private final RatingRepository storage;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final RequestRepository requestRepository;

    private final StatClient statClient;

    @Transactional
    public EventFullDto postRatingEvent(Long userId, Long eventId, Long like) {
        checkRequest(eventId, userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));
        Rating rating = Rating.builder()
                .pk(new RatingPK(user, event))
                .likeEvent(like)
                .build();
        storage.save(rating);
        EventFullDto fullDto = EventMapper.toFullDto(event);
        fullDto.setViews(viewsEvent("/events/" + event.getId()));
        fullDto.setConfirmedRequests(requestRepository.countByEventIdAndStatus(event.getId(), Status.CONFIRMED));
        fullDto.setRating(storage.sumLike(eventId));
        return fullDto;
    }

    private void checkRequest(Long eventId, Long userId) {
        Request request = requestRepository.findByEventIdAndRequesterIdAndStatus(eventId, userId, Status.CONFIRMED);
        if (request == null) {
            throw new ConflictException("Пользователь не регестрировался для участия в событии или не подтвержден как участник");
        }
    }

    private Long viewsEvent(String uris) {
        List<ViewStatsDto> dto = statClient.getStat(null, null, List.of(uris), false);
        return dto.size() > 0 ? dto.get(0).getHits() : 0L;
    }

    @Transactional
    public EventFullDto patchRatingEvent(Long userId, Long eventId, Long like) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));
        Rating rating = storage.findById(new RatingPK(user, event)).orElseThrow(() -> new DataNotFoundException("Rating User with id=" + userId + " and Event with id=" + eventId + "  was not found"));
        if (rating.getLikeEvent().equals(like)) {
            throw new ConflictException("Field: like. Error: Значение like совпадает с уже имеющимся. Value:" + like);
        } else {
            rating.setLikeEvent(like);
        }
        storage.save(rating);
        EventFullDto fullDto = EventMapper.toFullDto(event);
        fullDto.setViews(viewsEvent("/events/" + event.getId()));
        fullDto.setConfirmedRequests(requestRepository.countByEventIdAndStatus(event.getId(), Status.CONFIRMED));
        fullDto.setRating(storage.sumLike(eventId));
        return fullDto;
    }

    @Transactional
    public EventFullDto deleteRatingEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));
        RatingPK pk = new RatingPK(user, event);
        storage.findById(pk).orElseThrow(() -> new DataNotFoundException("Rating User with id=" + userId + " and Event with id=" + eventId + "  was not found"));
        storage.deleteById(pk);
        EventFullDto fullDto = EventMapper.toFullDto(event);
        fullDto.setViews(viewsEvent("/events/" + event.getId()));
        fullDto.setConfirmedRequests(requestRepository.countByEventIdAndStatus(event.getId(), Status.CONFIRMED));
        fullDto.setRating(storage.sumLike(eventId));
        return fullDto;
    }
}
