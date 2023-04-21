package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.StatClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.service.event.EventPublicService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
@Slf4j
public class EventPublicController {

    private final EventPublicService eventPublicService;

    private final StatClient statClient;

    @GetMapping(value = "/{id}")
    public EventFullDto getEventPub(@PathVariable Long id, HttpServletRequest request) {
        log.info("Get Event Public with id {}", id);
        return eventPublicService.getEventPub(id, request);
    }

    @GetMapping
    public List<EventShortDto> getEventPub(@RequestParam(required = false) String text,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false, defaultValue = "false") Boolean paid,
                                           @RequestParam(required = false) String rangeStart,
                                           @RequestParam(required = false) String rangeEnd,
                                           @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                           @RequestParam(required = false, defaultValue = "EVENT_DATE") String sort,
                                           @RequestParam(name = "from", defaultValue = "0") int from,
                                           @RequestParam(name = "size", defaultValue = "10") int size,
                                           HttpServletRequest request) {
        log.info("Get all Event Public with text {}, categories {}, paid {}, rangeStart {}, rangeEnd {}, onlyAvailable {}, sort {}, from {}, size {}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventPublicService.getSearchEventPub(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }
}
