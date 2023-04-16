package ru.practicum.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.exceptions.DataNotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.repository.CompilationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationPublicService {

    private final CompilationRepository storage;

    public  List<CompilationDto> getAllCompilationPub(Boolean pinned,int from,int size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Compilation> compilations=storage.findByPinned(pinned,pageable);
        return compilations.stream()
                .map(CompilationMapper::toDto)
                .collect(Collectors.toList());
    }

    public  CompilationDto getCompilationPub(Long compId) {
        Compilation compilations=storage.findById(compId).orElseThrow(() -> new DataNotFoundException("Compilation with id=" + compId + " was not found"));
        return CompilationMapper.toDto(compilations);
    }
}
