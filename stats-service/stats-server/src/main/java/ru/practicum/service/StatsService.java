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
        if (uris == null || uris.isEmpty() || uris.contains("/events")) {
            if (unique) {
                return storage.getStatsUnique(start, end);
            } else {
                return storage.getStats(start, end);
            }
        } else {
            // Убрать квадратные скобки, если один элемент
            if (uris.size() == 1) {
                uris.set(0, uris.get(0).replaceAll("^\\[|\\]$", ""));
            }
            if (unique) {
                return storage.getStatsUniqueUri(start, end, uris);
            } else {
                return storage.getStatsUri(start, end, uris);
            }
        }
    }
}
