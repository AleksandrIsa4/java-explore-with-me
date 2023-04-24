package ru.practicum.service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.DataNotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.enumeration.State;
import ru.practicum.model.enumeration.Status;
import ru.practicum.model.event.Event;
import ru.practicum.model.request.Request;
import ru.practicum.model.user.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestPrivateService {

    private final RequestRepository storage;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Transactional
    public ParticipationRequestDto saveRequestPriv(Long userId, Long eventId) {
        User initiator = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));
        checkPostRequest(event, userId);
        Request request;
        if (event.getRequestModeration()) {
            request = new Request(LocalDateTime.now(), event, initiator, Status.PENDING);
        } else {
            request = new Request(LocalDateTime.now(), event, initiator, Status.CONFIRMED);
        }
        request = storage.save(request);
        ParticipationRequestDto dto = RequestMapper.toDto(request);
        return RequestMapper.toDto(request);
    }

    private void checkPostRequest(Event event, Long userId) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (event.getState().equals(State.PENDING) || event.getState().equals(State.CANCELED)) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }
        Long countRequest = storage.countByEventId(event.getId());
        if (event.getParticipantLimit() <= countRequest) {
            throw new ConflictException("Достигнут лимит запросов на участие");
        }
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestPriv(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        List<Request> requests = storage.findAllByRequesterId(userId);
        return requests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParticipationRequestDto patchRequestPriv(Long userId, Long requesId) {
        userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Request request = storage.findById(requesId).orElseThrow(() -> new DataNotFoundException("Request with id=" + requesId + " was not found"));
        request.setStatus(Status.CANCELED);
        request = storage.save(request);
        return RequestMapper.toDto(request);
    }
}
