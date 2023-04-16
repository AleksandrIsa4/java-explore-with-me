package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.compilation.CompilationPublicService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
@Slf4j
public class CompilationPublicController {

    private final CompilationPublicService compilationPublicService;

    @GetMapping
    public List<CompilationDto> getAllCompilationPub(@RequestParam(defaultValue = "false") Boolean pinned,
                                                     @RequestParam(name = "from", defaultValue = "0") int from,
                                                     @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Get all Compilation Public with pinned {}, from {}, size {}", pinned,from,size);
        return compilationPublicService.getAllCompilationPub(pinned,from,size);
    }

    @GetMapping(value = "/{compId}")
    public CompilationDto getCompilationPub(@PathVariable Long compId) {
        log.info("Get Compilation Public with compId {}", compId);
        return compilationPublicService.getCompilationPub(compId);
    }
}
