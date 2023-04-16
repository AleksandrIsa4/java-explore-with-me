package ru.practicum.service.categorie;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.DataNotFoundException;
import ru.practicum.model.categorie.Categorie;
import ru.practicum.repository.CategoriesRepository;

@Service
@RequiredArgsConstructor
public class CategoriesService {

    private final CategoriesRepository storage;

    public Categorie save(Categorie categorie) {
        return storage.save(categorie);
    }

    public void delete(Long id) {
        storage.findById(id).orElseThrow(() -> new DataNotFoundException("Categorie with id=" + id + " was not found"));
        storage.deleteById(id);
    }

    public Categorie patch(Categorie categorie, Long id) {
        storage.findById(id).orElseThrow(() -> new DataNotFoundException("Categorie with id=" + id + " was not found"));
        categorie.setId(id);
        return storage.save(categorie);
    }
}
