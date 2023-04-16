package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.DataNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.categorie.Categorie;
import ru.practicum.model.event.Event;
import ru.practicum.model.enumeration.State;
import ru.practicum.model.enumeration.Status;
import ru.practicum.model.request.Request;
import ru.practicum.model.user.User;
import ru.practicum.repository.CategoriesRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.lang.reflect.Field;
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
        Event event = EventMapper.toEntity(dto);
        if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new ConflictException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value:" + dto.getEventDate());
        }
        event.setCreatedOn(LocalDateTime.now());
        Categorie categorie = categoriesRepository.findById(dto.getCategory()).orElseThrow(() -> new DataNotFoundException("Category with id=" + dto.getCategory() + " was not found"));
        event.setCategory(categorie);
        User initiator = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        event.setInitiator(initiator);
        event.setState(State.PENDING);
        event = storage.save(event);
        EventFullDto fullDto = EventMapper.toFullDto(event);
        UserShortDto userShortDto= UserMapper.toDtoShort(initiator);
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

    public EventFullDto patchEventPriv(UpdateEventUserRequest dto,Long userId, Long eventId) {
        User initiator = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        storage.findById(eventId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));
        Event event=storage.findByInitiatorIdAndId(initiator.getId(), eventId);
        if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new ConflictException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value:" + dto.getEventDate());
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        event=updateEventUserPriv(dto,event);
        EventFullDto fullDto = EventMapper.toFullDto(event);
        return fullDto;
    }

    private Event updateEventUserPriv(UpdateEventUserRequest dto,Event event){
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            Categorie categorieDto = categoriesRepository.findById(dto.getCategory()).orElseThrow(() -> new DataNotFoundException("Category with id=" + dto.getCategory() + " was not found"));
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
        event = storage.save(event);
/*        if (dto.getStateAction() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }*/
        return event;
    }

    public List<ParticipationRequestDto> getEventRequestsPriv(Long userId, Long eventId) {
        User initiator = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Event event = storage.findById(eventId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));
        List<Request> requests = requestRepository.findAllByRequesterIdAndEventId(userId,eventId);
        return requests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public EventRequestStatusUpdateResult patchEventRequestsPriv(EventRequestStatusUpdateRequest dto,Long userId, Long eventId) {
        User initiator = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Event event = storage.findById(eventId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));
        List<Request> requests = requestRepository.findByIdIn(dto.getRequestIds());
        List<ParticipationRequestDto> confirmedRequests=new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests=new ArrayList<>();
        if(Status.CONFIRMED.equals(dto.getStatus())){
            int cancelIndex=0;
            for(int i=0;i<event.getParticipantLimit();i++){
                requests.get(i).setStatus(Status.CONFIRMED);
                cancelIndex=i;
                confirmedRequests.add(RequestMapper.toDto(requests.get(i)));
            }
            for(int i=cancelIndex;i<requests.size();i++){
                requests.get(i).setStatus(Status.CANCELED);
                rejectedRequests.add(RequestMapper.toDto(requests.get(i)));
            }
        } else {
            for(int i=0;i<requests.size();i++){
                requests.get(i).setStatus(Status.CANCELED);
                rejectedRequests.add(RequestMapper.toDto(requests.get(i)));
            }
        }
        return new EventRequestStatusUpdateResult(confirmedRequests,rejectedRequests);
    }
}
