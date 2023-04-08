package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStatsDto;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository storage;

    public EndpointHit save(EndpointHit endpointHit) {
        return storage.save(endpointHit);
    }

    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return storage.getStatsUnique(start, end);
            } else {
                return storage.getStats(start, end);
            }
        } else {
            if (unique) {
                return storage.getStatsUniqueUri(start, end, uris);
            } else {
                return storage.getStatsUri(start, end, uris);
            }
        }
    }
}
