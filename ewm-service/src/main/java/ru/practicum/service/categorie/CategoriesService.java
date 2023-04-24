package ru.practicum.service.categorie;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.DataNotFoundException;
import ru.practicum.model.categorie.Category;
import ru.practicum.repository.CategoriesRepository;

@Service
@RequiredArgsConstructor
public class CategoriesService {

    private final CategoriesRepository storage;

    public Category save(Category categorie) {
        return storage.save(categorie);
    }

    public void delete(Long id) {
        storage.findById(id).orElseThrow(() -> new DataNotFoundException("Categorie with id=" + id + " was not found"));
        storage.deleteById(id);
    }

    public Category patch(Category categorie, Long id) {
        storage.findById(id).orElseThrow(() -> new DataNotFoundException("Categorie with id=" + id + " was not found"));
        categorie.setId(id);
        return storage.save(categorie);
    }
}
