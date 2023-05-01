package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.model.enumeration.Grade;
import ru.practicum.service.rating.RatingPrivateService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class RatingPrivateController {

    private final RatingPrivateService ratingPrivateService;

    @PostMapping(value = "/{userId}/events/{eventId}/rating")
    public EventFullDto postRatingEvent(@PathVariable Long userId, @PathVariable Long eventId, @RequestParam String like) {
        log.info("Post Rating with userId={}, eventId={}, like={}", userId, eventId, like);
        return ratingPrivateService.postRatingEvent(userId, eventId, Grade.valueOf(like).getScore());
    }

    @PatchMapping(value = "/{userId}/events/{eventId}/rating")
    public EventFullDto patchRatingEvent(@PathVariable Long userId, @PathVariable Long eventId, @RequestParam String like) {
        log.info("Patch Rating with userId={}, eventId={}, like={}", userId, eventId, like);
        return ratingPrivateService.patchRatingEvent(userId, eventId, Grade.valueOf(like).getScore());
    }

    @DeleteMapping(value = "/{userId}/events/{eventId}/rating")
    public EventFullDto deleteRatingEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Post Rating with userId={}, eventId={}", userId, eventId);
        return ratingPrivateService.deleteRatingEvent(userId, eventId);
    }
}
