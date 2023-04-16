package ru.practicum.service.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.client.StatClient;
import ru.practicum.dto.categorie.CategoryDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.exceptions.DataNotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.ViewStatsDto;
import ru.practicum.model.categorie.Categorie;
import ru.practicum.model.enumeration.State;
import ru.practicum.model.enumeration.Status;
import ru.practicum.model.event.Event;
import ru.practicum.repository.CategoriesRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventPublicService {

    private final EventRepository storage;

    private final CategoriesRepository categoriesRepository;

    private final RequestRepository requestRepository;

    private final StatClient statClient;

    public EventFullDto getEventPub(Long id) {
        Event event = storage.findById(id).orElseThrow(() -> new DataNotFoundException("Event with id=" + id + " was not found"));
        EventFullDto fullDto = EventMapper.toFullDto(event);
        return fullDto;
    }

    public List<EventShortDto> getSearchEventPub(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, int from, int size) {
        LocalDateTime dateStartSearch = LocalDateTime.now().plusSeconds(1L);
        LocalDateTime dateEndSearch = LocalDateTime.now().plusYears(99L);
        if (rangeStart != null) {
            dateStartSearch = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (rangeEnd != null) {
            dateEndSearch = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (categories == null || categories.size() == 0) {
            categories = categoriesRepository.findAll().stream()
                    .map(c -> c.getId())
                    .collect(Collectors.toList());
        }
        Pageable pageable = PageRequest.of(from, size);
        List<Event> events = storage.SearchEventPub(text, categories, paid, dateStartSearch, dateEndSearch, pageable);
        if(onlyAvailable){
            events=events.stream()
                    .filter(e -> e.getParticipantLimit()>confirmedRequests(e.getId()))
                    .collect(Collectors.toList());
        }
        List<EventShortDto> eventShorts=events.stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());



        if (sort.equals("VIEWS")) {
/*            List<String> uris;
            for(EventShortDto eventShortDto:eventShorts){
                uris.add()
            }*/
            eventShorts.stream()
                    .peek(e -> e.setViews(viewsEvent(rangeStart,rangeEnd,"event/"+e.getId(),false)))
                    .sorted(Comparator.comparing(EventShortDto::getViews));
        }





        return eventShorts;
    }

    private Long confirmedRequests(Long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, Status.CONFIRMED);
    }

    private Long viewsEvent (String rangeStart, String rangeEnd,String uris, Boolean unique){
      //  ViewStatsDto viewStatsDto=statClient.getStat(rangeStart, rangeEnd, List.of(uris), unique);
        ViewStatsDto viewStatsDto= new ObjectMapper().convertValue(statClient.getStat(rangeStart, rangeEnd, List.of(uris), unique), new TypeReference<>() {});
        return viewStatsDto.getHits();
    }




}
