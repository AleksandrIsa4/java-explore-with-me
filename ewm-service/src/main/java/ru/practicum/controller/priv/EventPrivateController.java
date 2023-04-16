package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.event.EventPrivateService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class EventPrivateController {

    private final EventPrivateService eventPrivateService;

    @PostMapping(value = "/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postEventPriv(@RequestBody @Valid NewEventDto dto, @PathVariable Long userId) {
        log.info("Post Event with dto {}, userId {}", dto, userId);
        return eventPrivateService.saveEventPriv(dto,userId);
    }

    @GetMapping(value = "/{userId}/events")
    public List<EventShortDto> getEventsPriv(@PathVariable Long userId,
                                       @RequestParam(name = "from", defaultValue = "0") int from,
                                       @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Get Events with userId={}, from={}, size={}", userId,from,size);
        return eventPrivateService.getEventPriv(userId,from,size);
    }

    @GetMapping(value = "/{userId}/events/{eventId}")
    public EventFullDto getEventFullPriv(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Get Event with userId={}, eventId={}", userId,eventId);
        return eventPrivateService.getEventFullPriv(userId,eventId);
    }

    @PatchMapping(value = "/{userId}/events/{eventId}")
    public EventFullDto getEventFullPriv(@RequestBody @Valid UpdateEventUserRequest dto, @PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Patch Event with userId={}, eventId={}", userId,eventId);
        return eventPrivateService.patchEventPriv(dto,userId,eventId);
    }

    @GetMapping(value = "/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequestsPriv(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Get Event Request  with userId={}, eventId={}", userId,eventId);
        return eventPrivateService.getEventRequestsPriv(userId,eventId);
    }

    @PatchMapping(value = "/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult patchEventRequestsPriv(@PathVariable Long userId, @PathVariable Long eventId,@RequestBody EventRequestStatusUpdateRequest dto) {
        log.info("Patch Event Request with userId={}, eventId={}, dto={}", userId,eventId, dto);
        return eventPrivateService.patchEventRequestsPriv(dto,userId,eventId);
    }

}
