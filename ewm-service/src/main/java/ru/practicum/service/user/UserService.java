package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.DataNotFoundException;
import ru.practicum.model.user.User;
import ru.practicum.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository storage;

    public User save(User user) {
        return storage.save(user);
    }

    public void delete(Long id) {
        storage.findById(id).orElseThrow(() -> new DataNotFoundException("User with id=" + id + " was not found"));
        storage.deleteById(id);
    }

    public List<User> get(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        List<User> users = storage.findByIdIn(ids, pageable);
        return users;
    }
}
