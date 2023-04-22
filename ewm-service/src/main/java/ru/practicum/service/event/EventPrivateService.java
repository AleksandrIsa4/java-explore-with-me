package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.DataNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.categorie.Category;
import ru.practicum.model.enumeration.StateAction;
import ru.practicum.model.event.Event;
import ru.practicum.model.enumeration.State;
import ru.practicum.model.enumeration.Status;
import ru.practicum.model.request.Request;
import ru.practicum.model.user.User;
import ru.practicum.repository.CategoriesRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventPrivateService {

    private final EventRepository storage;

    private final CategoriesRepository categoriesRepository;

    private final UserRepository userRepository;

    private final RequestRepository requestRepository;


    public EventFullDto saveEventPriv(NewEventDto dto, Long userId) {
        if (dto.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ConflictException("Field: eventDate. Error: Должно содержать дату, которая еще не наступила. Value:" + dto.getEventDate());
        }
        if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new BadRequestException("Field: eventDate. Error: Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента. Value:" + dto.getEventDate());
        }
        Category categorie = categoriesRepository.findById(dto.getCategory()).orElseThrow(() -> new DataNotFoundException("Category with id=" + dto.getCategory() + " was not found"));
        User initiator = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Event event = EventMapper.toEntitySave(dto,categorie,initiator);
        event = storage.save(event);
        EventFullDto fullDto = EventMapper.toFullDto(event);
        UserShortDto userShortDto = UserMapper.toDtoShort(initiator);
        fullDto.setInitiator(userShortDto);
        return fullDto;
    }

    public List<EventShortDto> getEventPriv(Long userId, int from, int size) {
        User initiator = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Pageable pageable = PageRequest.of(from, size);
        List<Event> events = storage.findAllByInitiatorId(initiator.getId(), pageable);
        return events.stream()
                .map(EventMapper::toShortDto)
                .peek(e -> e.setConfirmedRequests(requestRepository.countByEventIdAndStatus(e.getId(), Status.CONFIRMED)))
                .collect(Collectors.toList());
    }

    public EventFullDto getEventFullPriv(Long userId, Long eventId) {
        User initiator = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        storage.findById(eventId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));
        Event event = storage.findByInitiatorIdAndId(initiator.getId(), eventId);
        EventFullDto fullDto = EventMapper.toFullDto(event);
        return fullDto;
    }

    public EventFullDto patchEventPriv(UpdateEventUserRequest dto, Long userId, Long eventId) {
        User initiator = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        storage.findById(eventId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));
        Event event = storage.findByInitiatorIdAndId(initiator.getId(), eventId);
        if (dto.getEventDate() != null) {
            if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2L)) && dto.getEventDate() != null) {
                throw new ConflictException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента. dto:" + dto.getEventDate());
            }
        }
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }
        event = updateEventUserPriv(dto, event);
        EventFullDto fullDto = EventMapper.toFullDto(event);
        return fullDto;
    }

    private Event updateEventUserPriv(UpdateEventUserRequest dto, Event event) {
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
        if (dto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
            event.setState(State.PENDING);
        }
        if (dto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
            event.setState(State.CANCELED);
        }
        event = storage.save(event);
        return event;
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequestsPriv(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        storage.findById(eventId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventRequestStatusUpdateResult patchEventRequestsPriv(EventRequestStatusUpdateRequest dto, Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Event event = storage.findById(eventId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));
        List<Request> requests = requestRepository.findByIdIn(dto.getRequestIds());
        Long countRequest = requestRepository.countByEventId(event.getId());
        if (event.getParticipantLimit() <= countRequest) {
            throw new ConflictException("Достигнут лимит запросов на участие");
        }
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        // Подтверждение части заявок и часть отклонить, если заявок больше, чем доступно или отклонение всех заявок
        if (Status.CONFIRMED.equals(dto.getStatus()) && requests.size() > 0) {
            int cancelIndex = 0;
            int maxIndex = maxIndexResult(event.getParticipantLimit(), requests.size());
            for (int i = 0; i < maxIndex; i++) {
                requests.get(i).setStatus(Status.CONFIRMED);
                requestRepository.save(requests.get(i));
                cancelIndex++;
                confirmedRequests.add(RequestMapper.toDto(requests.get(i)));
            }
            for (int i = cancelIndex; i < requests.size(); i++) {
                checkStatus(requests.get(i));
                requests.get(i).setStatus(Status.REJECTED);
                requestRepository.save(requests.get(i));
                rejectedRequests.add(RequestMapper.toDto(requests.get(i)));
            }
        } else {
            for (int i = 0; i < requests.size(); i++) {
                checkStatus(requests.get(i));
                requests.get(i).setStatus(Status.REJECTED);
                requestRepository.save(requests.get(i));
                rejectedRequests.add(RequestMapper.toDto(requests.get(i)));
            }
        }
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    private void checkStatus(Request request) {
        if (request.getStatus().equals(Status.CONFIRMED)) {
            throw new ConflictException("Cтатус можно изменить только у заявок, находящихся в состоянии ожидания");
        }
    }

    private int maxIndexResult(Long a, int b) {
        int maxIndex;
        if (a <= b) {
            maxIndex = a.intValue();
        } else {
            maxIndex = b;
        }
        return maxIndex;
    }
}
