package ru.practicum.service.categorie;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.categorie.CategoryDto;
import ru.practicum.exceptions.DataNotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.categorie.Category;
import ru.practicum.repository.CategoriesRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriesPublicService {

    private final CategoriesRepository storage;

    public List<CategoryDto> getAllCategoriesPub(int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        Page<Category> categories = storage.findAll(pageable);
        return categories.stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoriesPub(Long catId) {
        Category categorie = storage.findById(catId).orElseThrow(() -> new DataNotFoundException("Category with id=" + catId + " was not found"));
        return CategoryMapper.toDto(categorie);
    }
}
