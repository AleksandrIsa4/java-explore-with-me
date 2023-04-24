package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.categorie.CategoryDto;
import ru.practicum.dto.categorie.NewCategoryDto;
import ru.practicum.model.categorie.Category;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CategoryMapper {

    public static CategoryDto toDto(Category categorie) {
        return CategoryDto.builder()
                .id(categorie.getId())
                .name(categorie.getName())
                .build();
    }

    public static Category toEntity(NewCategoryDto dto) {
        return Category.builder()
                .name(dto.getName())
                .build();
    }
}
