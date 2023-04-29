package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.exceptions.DataNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.ViewStatsDto;
import ru.practicum.model.enumeration.State;
import ru.practicum.model.enumeration.Status;
import ru.practicum.model.event.Event;
import ru.practicum.repository.CategoriesRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RatingRepository;
import ru.practicum.repository.RequestRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventPublicService {

    private final EventRepository storage;

    private final CategoriesRepository categoriesRepository;

    private final RequestRepository requestRepository;

    private final RatingRepository ratingRepository;

    private final StatClient statClient;

    public EventFullDto getEventPub(Long id, HttpServletRequest request) {
        Event event = storage.findById(id).orElseThrow(() -> new DataNotFoundException("Event with id=" + id + " was not found"));
        EventFullDto fullDto = EventMapper.toFullDto(event);
        fullDto.setRating(ratingRepository.sumLike(id));
        saveHit(request, id);
        return fullDto;
    }

    @Transactional
    public List<EventShortDto> getSearchEventPub(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, int from, int size, HttpServletRequest request) {
        LocalDateTime dateStartSearch = LocalDateTime.now().plusSeconds(1L);
        LocalDateTime dateEndSearch = LocalDateTime.now().plusYears(99L);
        if (rangeStart != null) {
            dateStartSearch = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (rangeEnd != null) {
            dateEndSearch = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (categories == null || categories.size() == 0) {
            categories = categoriesRepository.findAll().stream()
                    .map(c -> c.getId())
                    .collect(Collectors.toList());
        }
        Pageable pageable = PageRequest.of(from, size);
        List<Event> events = storage.searchEventPub(text, categories, paid, dateStartSearch, dateEndSearch, State.PUBLISHED, pageable);
        // Только события у которых не исчерпан лимит запросов на участие
        if (onlyAvailable) {
            events = events.stream()
                    .filter(e -> e.getParticipantLimit() > confirmedRequests(e.getId()))
                    .collect(Collectors.toList());
        }
        List<EventShortDto> eventShorts = events.stream()
                .map(EventMapper::toShortDto)
                .peek(e -> e.setViews(viewsEvent(rangeStart, rangeEnd, "/events/" + e.getId(), false)))
                .peek(e -> e.setRating(ratingRepository.sumLike(e.getId())))
                .collect(Collectors.toList());
        // Вариант сортировки
        switch (sort) {
            case "VIEWS":
                eventShorts = eventShorts.stream()
                        .sorted(Comparator.comparing(EventShortDto::getViews))
                        .collect(Collectors.toList());
                break;
            case "RATING":
                eventShorts = eventShorts.stream()
                        .sorted(Comparator.comparing(EventShortDto::getRating).reversed())
                        .collect(Collectors.toList());
                break;
        }
        saveHit(request, null);
        return eventShorts;
    }

    private Long confirmedRequests(Long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, Status.CONFIRMED);
    }

    private Long viewsEvent(String rangeStart, String rangeEnd, String uris, Boolean unique) {
        List<ViewStatsDto> dto = statClient.getStat(rangeStart, rangeEnd, List.of(uris), unique);
        return dto.size() > 0 ? dto.get(0).getHits() : 0L;
    }

    private void saveHit(HttpServletRequest request, Long eventId) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("ewm-service")
                .timestamp(LocalDateTime.now())
                .ip(request.getRemoteAddr())
                .build();
        if (eventId == null) {
            endpointHitDto.setUri("/events");
        } else {
            endpointHitDto.setUri("/events/" + eventId);
        }
        statClient.postStat(endpointHitDto);
    }
}
