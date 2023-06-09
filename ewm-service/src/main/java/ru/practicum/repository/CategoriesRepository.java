package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.categorie.Category;

@Repository
public interface CategoriesRepository extends JpaRepository<Category, Long> {
}
