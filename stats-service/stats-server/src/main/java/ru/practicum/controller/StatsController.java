package ru.practicum.controller;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping(value = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto save(@RequestBody EndpointHitDto dto) {
        log.info("Post hit with dto {}", dto);
        EndpointHit endpointHit = EndpointHitMapper.toEntity(dto);
        endpointHit = statsService.save(endpointHit);
        return EndpointHitMapper.toDto(endpointHit);
    }

    @GetMapping(value = "/stats")
    public List<ViewStatsDto> get(@RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                  @RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(defaultValue = "false") boolean unique
    ) {
        log.info("Get stats with start {}, end {}, uris {}, unique {}", start, end, uris, unique);
        List<ViewStatsDto> viewStatsDtos = statsService.get(start, end, uris, unique);
        return viewStatsDtos;
    }
}
