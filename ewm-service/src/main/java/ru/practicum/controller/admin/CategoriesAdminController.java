package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.categorie.CategoryDto;
import ru.practicum.dto.categorie.NewCategoryDto;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.categorie.Categorie;
import ru.practicum.service.categorie.CategoriesService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
@Slf4j
public class CategoriesAdminController {

    private final CategoriesService categoriesService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto postCategorie(@RequestBody NewCategoryDto dto) {
        log.info("Post Categorie with dto {}", dto);
        Categorie categorie = CategoryMapper.toEntity(dto);
        categorie = categoriesService.save(categorie);
        return CategoryMapper.toDto(categorie);
    }

    @DeleteMapping(value = "/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategorie(@PathVariable Long catId) {
        log.info("Delete Categorie with catId={}", catId);
        categoriesService.delete(catId);
    }

    @PatchMapping(value = "/{catId}")
    public CategoryDto patchCategorie(@RequestBody @Valid NewCategoryDto dto, @PathVariable Long catId) {
        log.info("Patch Categorie with dto={}, catId={}", dto,catId);
        Categorie categorie = CategoryMapper.toEntity(dto);
        categorie = categoriesService.patch(categorie,catId);
        return CategoryMapper.toDto(categorie);
    }
}
