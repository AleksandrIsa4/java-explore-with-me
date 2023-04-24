package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.service.compilation.CompilationAdminService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
@Slf4j
public class CompilationAdminController {

    private final CompilationAdminService compilationAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto postCompilation(@RequestBody @Valid NewCompilationDto dto) {
        log.info("Post Compilation with dto {}", dto);
        return compilationAdminService.postCompilation(dto);
    }

    @DeleteMapping(value = "/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Delete Compilation with compId={}", compId);
        compilationAdminService.deleteCompilation(compId);
    }

    @PatchMapping(value = "/{compId}")
    public CompilationDto patchEventAdmin(@PathVariable Long compId, @RequestBody UpdateCompilationRequest dto) {
        log.info("Patch Admin Event with dto={}, compId={}}", dto, compId);
        return compilationAdminService.patchCompilationAdmin(dto, compId);
    }
}
