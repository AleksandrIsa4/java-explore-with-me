package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.categorie.CategoryDto;
import ru.practicum.service.categorie.CategoriesPublicService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
@Slf4j
public class CategoriesPublicController {


    private final CategoriesPublicService categoriesPublicService;

    @GetMapping
    public List<CategoryDto> getAllCompilationPub(@RequestParam(name = "from", defaultValue = "0") int from,
                                                  @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Get all Categories Public with from {}, size {}", from, size);
        return categoriesPublicService.getAllCategoriesPub(from, size);
    }

    @GetMapping(value = "/{catId}")
    public CategoryDto getCompilationPub(@PathVariable Long catId) {
        log.info("Get Categories Public with catId {}", catId);
        return categoriesPublicService.getCategoriesPub(catId);
    }
}
