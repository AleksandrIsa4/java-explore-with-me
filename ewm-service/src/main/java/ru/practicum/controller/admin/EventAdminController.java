package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.service.event.EventAdminService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
@Slf4j
public class EventAdminController {

    private final EventAdminService eventAdminService;

    @GetMapping
    public List<EventFullDto> getEventAdmin(@RequestParam(required = false) List<Long> users,
                                            @RequestParam(required = false) List<String> states,
                                            @RequestParam(required = false) List<Long> categories,
                                            @RequestParam(required = false) String rangeStart,
                                            @RequestParam(required = false) String rangeEnd,
                                            @RequestParam(name = "from", defaultValue = "0") int from,
                                            @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Get Admin Event with users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={},", users, states, categories, rangeStart, rangeEnd, from, size);
        return eventAdminService.getEventAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping(value = "/{eventId}")
    public EventFullDto patchEventAdmin(@PathVariable Long eventId, @RequestBody @Valid UpdateEventAdminRequest dto) {
        log.info("Patch Admin Event with dto={}, eventId={}}", dto, eventId);
        return eventAdminService.patchEventAdmin(dto, eventId);
    }
}
