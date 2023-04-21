package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.request.RequestPrivateService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class RequestPrivateController {

    private final RequestPrivateService requestPrivateService;

    @PostMapping(value = "/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto postRequestPriv(@PathVariable Long userId, @RequestParam @NotNull long eventId) {
        log.info("Post Request with userId {}, eventId {}", userId, eventId);
        return requestPrivateService.saveRequestPriv(userId, eventId);
    }

    @GetMapping(value = "/{userId}/requests")
    public List<ParticipationRequestDto> getRequestPriv(@PathVariable Long userId) {
        log.info("Get Request with userId {}", userId);
        return requestPrivateService.getRequestPriv(userId);
    }

    @PatchMapping(value = "/{userId}/requests/{requesId}/cancel")
    public ParticipationRequestDto patchRequestPriv(@PathVariable Long userId, @PathVariable Long requesId) {
        log.info("Patch Request with userId {}, requesId {}", userId, requesId);
        return requestPrivateService.patchRequestPriv(userId, requesId);
    }
}
