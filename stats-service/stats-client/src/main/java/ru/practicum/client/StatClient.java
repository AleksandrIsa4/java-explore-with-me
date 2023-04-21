package ru.practicum.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.ViewStatsDto;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatClient extends BaseClient {

    @Autowired
    public StatClient(@Value("${stat-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> postStat(EndpointHitDto dto) {
        log.info("StatClient Post hit with dto {}", dto);
        return post("/hit", dto);
    }

    public List<ViewStatsDto> getStat(String start, String end, List<String> uris, boolean unique) {
        log.info("StatClient Get stats with start {}, end {}, uris {}, unique {}", start, end, uris, unique);
        if (start == null) {
            start = "1900-01-01 01:01:01";
        }
        if (end == null) {
            end = "2200-01-01 01:01:01";
        }
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        ResponseEntity<Object> objectResponseEntity = get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        List<ViewStatsDto> viewStatsDto = new ObjectMapper().convertValue(objectResponseEntity.getBody(), new TypeReference<List<ViewStatsDto>>() {
        });
        return viewStatsDto;
    }
}
