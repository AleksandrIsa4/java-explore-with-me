package ru.practicum.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.DataNotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.categorie.Categorie;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.enumeration.State;
import ru.practicum.model.event.Event;
import ru.practicum.model.user.User;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationAdminService {

    private final CompilationRepository storage;

    private final EventRepository eventRepository;

    public CompilationDto postCompilation(NewCompilationDto dto) {
        Compilation compilation = CompilationMapper.toEntity(dto);
        List<Event> events = eventRepository.findAllById(dto.getEvents());
        compilation.setEvents(events);
        compilation = storage.save(compilation);
        return CompilationMapper.toDto(compilation);
    }

    public void deleteCompilation(Long id) {
        storage.findById(id).orElseThrow(() -> new DataNotFoundException("Compilation with id=" + id + " was not found"));
        storage.deleteById(id);
    }

    public CompilationDto patchCompilationAdmin(UpdateCompilationRequest dto, Long compId) {
        Compilation compilation = storage.findById(compId).orElseThrow(() -> new DataNotFoundException("Compilation with id=" + compId + " was not found"));
        compilation = updateCompilationAdmin(dto, compilation);
        return CompilationMapper.toDto(compilation);
    }

    private Compilation updateCompilationAdmin(UpdateCompilationRequest dto, Compilation compilation) {
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getEvents() != null || dto.getEvents().size() != 0) {
            List<Event> events=eventRepository.findAllById(dto.getEvents());
            compilation.setEvents(events);
        }
        compilation = storage.save(compilation);
        return compilation;
    }


}
