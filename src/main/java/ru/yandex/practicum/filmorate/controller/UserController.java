package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserInterface;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Set;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private static final String USER_ID_PATH = "/{id}";
    private static final String FRIENDS_PATH = USER_ID_PATH + "/friends";
    private static final String FRIEND_ID_PATH = FRIENDS_PATH + "/{friendId}";
    private static final String COMMON_FRIENDS_PATH = USER_ID_PATH + "/friends/common/{otherId}";

    private final UserStorage userStorage;
    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @GetMapping(USER_ID_PATH)
    public User findById(@PathVariable("id") Long id) {
        return userStorage.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return userStorage.update(newUser);
    }

    @PutMapping(FRIEND_ID_PATH)
    public User addFriend(@Valid @PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping(FRIEND_ID_PATH)
    public User delFriend(@Valid @PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        return userService.delFriend(id, friendId);
    }

    @GetMapping(COMMON_FRIENDS_PATH)
    public Set<User> findJointFriends(@Valid @PathVariable("id") Long id, @PathVariable("otherId") Long otherId) {
        return userService.findJointFriends(id, otherId);
    }

    @GetMapping(FRIENDS_PATH)
    public Set<User> findAllFriends(@Valid @PathVariable("id") Long id) {
        return userService.findAllFriends(id);
    }
}