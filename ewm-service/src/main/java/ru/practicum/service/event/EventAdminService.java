package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.DataNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.ViewStatsDto;
import ru.practicum.model.categorie.Category;
import ru.practicum.model.enumeration.State;
import ru.practicum.model.enumeration.StateAction;
import ru.practicum.model.enumeration.Status;
import ru.practicum.model.event.Event;
import ru.practicum.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventAdminService {

    private final EventRepository storage;

    private final CategoriesRepository categoriesRepository;

    private final UserRepository userRepository;

    private final RequestRepository requestRepository;

    private final RatingRepository ratingRepository;

    private final StatClient statClient;


    public List<EventFullDto> getEventAdmin(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, int from, int size) {
        List<State> statesEnum = new ArrayList<>();
        if (states != null) {
            for (String state : states) {
                statesEnum.add(State.valueOf(state));
            }
        } else {
            statesEnum = Arrays.asList(State.values());
        }
        LocalDateTime dateStartSearch = LocalDateTime.now().plusYears(99L);
        LocalDateTime dateEndSearch = LocalDateTime.now().minusYears(99L);
        if (rangeStart != null) {
            dateStartSearch = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (rangeEnd != null) {
            dateEndSearch = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (users == null || users.size() == 0) {
            users = userRepository.findAll().stream()
                    .map((u) -> u.getId())
                    .collect(Collectors.toList());
        }
        if (categories == null || categories.size() == 0) {
            categories = categoriesRepository.findAll().stream()
                    .map((x) -> x.getId())
                    .collect(Collectors.toList());
        }
        Pageable pageable = PageRequest.of(from, size);
        List<Event> events = storage.findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateIsAfterAndEventDateIsBefore(users, statesEnum, categories, dateStartSearch, dateEndSearch, pageable);
        return events.stream()
                .map(EventMapper::toFullDto)
                .peek(e -> e.setConfirmedRequests(requestRepository.countByEventIdAndStatus(e.getId(), Status.CONFIRMED)))
                .peek(e -> e.setViews(viewsEvent(rangeStart, rangeEnd, "/events/" + e.getId(), false)))
                .collect(Collectors.toList());
    }

    private Long viewsEvent(String rangeStart, String rangeEnd, String uris, Boolean unique) {
        List<ViewStatsDto> dto = statClient.getStat(rangeStart, rangeEnd, List.of(uris), unique);
        return dto.size() > 0 ? dto.get(0).getHits() : 0L;
    }

    public EventFullDto patchEventAdmin(UpdateEventAdminRequest dto, Long eventId) {
        Event event = storage.findById(eventId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));
        if (dto.getEventDate() != null) {
            if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(1L))) {
                throw new ConflictException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value:" + dto.getEventDate());
            }
        }
        if (event.getState().equals(State.PUBLISHED) || event.getState().equals(State.CANCELED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        event = updateEventAdmin(dto, event);
        EventFullDto fullDto = EventMapper.toFullDto(event);
        fullDto.setViews(viewsEvent(null, null, "/events/" + eventId, false));
        fullDto.setRating(ratingRepository.sumLike(eventId));
        return fullDto;
    }

    private Event updateEventAdmin(UpdateEventAdminRequest dto, Event event) {
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            Category categorieDto = categoriesRepository.findById(dto.getCategory()).orElseThrow(() -> new DataNotFoundException("Category with id=" + dto.getCategory() + " was not found"));
            event.setCategory(categorieDto);
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            event.setLocation(dto.getLocation());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getStateAction().equals(StateAction.PUBLISH_EVENT) && event.getState().equals(State.PENDING)) {
            event.setState(State.PUBLISHED);
        }
        if (dto.getStateAction().equals(StateAction.REJECT_EVENT) && !event.getState().equals(State.PUBLISHED)) {
            event.setState(State.CANCELED);
        }
        event = storage.save(event);
        return event;
    }
}
