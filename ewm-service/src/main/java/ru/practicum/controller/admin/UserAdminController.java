package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.user.User;
import ru.practicum.service.user.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
@Slf4j
public class UserAdminController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto postUser(@RequestBody @Valid NewUserRequest dto) {
        log.info("Post User with dto {}", dto);
        User user = UserMapper.toEntity(dto);
        user = userService.save(user);
        return UserMapper.toDto(user);
    }

    @DeleteMapping(value = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Delete User with userId={}", userId);
        userService.delete(userId);
    }

    @GetMapping
    public List<UserDto> getUser(@RequestParam List<Long> ids,
                                 @RequestParam(name = "from", defaultValue = "0") int from,
                                 @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Get User with ids={}", ids);
        return userService.get(ids, from, size).stream().map(UserMapper::toDto).collect(Collectors.toList());
    }
}
