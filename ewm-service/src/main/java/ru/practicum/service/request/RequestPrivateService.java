package ru.practicum.service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.exceptions.DataNotFoundException;
import ru.practicum.mapper.RequestMapper;
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

    public ParticipationRequestDto saveRequestPriv(Long userId, Long eventId) {
        User initiator = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));
        Request request = new Request(LocalDateTime.now(), event, initiator, Status.PENDING);
        request = storage.save(request);
        return RequestMapper.toDto(request);
    }

    public List<ParticipationRequestDto> getRequestPriv(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        List<Request> requests = storage.findAllByRequesterId(userId);
        return requests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto patchRequestPriv(Long userId,Long requesId) {
        User user=userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Request request = storage.findById(requesId).orElseThrow(() -> new DataNotFoundException("Request with id=" + requesId + " was not found"));
        request.setStatus(Status.CANCELED);
        request = storage.save(request);
        return RequestMapper.toDto(request);
    }
}
