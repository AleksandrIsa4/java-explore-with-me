package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.categorie.CategoryDto;
import ru.practicum.dto.categorie.NewCategoryDto;
import ru.practicum.model.categorie.Categorie;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CategoryMapper {

    public static CategoryDto toDto(Categorie categorie) {
        return CategoryDto.builder()
                .id(categorie.getId())
                .name(categorie.getName())
                .build();
    }

    public static Categorie toEntity(NewCategoryDto dto) {
        return Categorie.builder()
                .name(dto.getName())
                .build();
    }
}
