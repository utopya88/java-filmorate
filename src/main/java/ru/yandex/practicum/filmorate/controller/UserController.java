package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FilmService filmService;

    @GetMapping
    public Collection<User> findAll() {

        return userService.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @GetMapping("{id}")
    public User getById(@PathVariable int id) {
        return userService.findUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id,
                          @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id,
                             @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriendsFromUser(@PathVariable int id) {
        return userService.getFriendsFromUser(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriendsFromUser(@PathVariable int id,
                                                     @PathVariable int otherId) {
        return userService.getCommonFriendsFromUser(id, otherId);
    }

    @GetMapping("/{userId}/feed")
    public Collection<Feed> getFeed(@PathVariable Integer userId) {
        return userService.getFeedByUserId(userId);
    }

    @DeleteMapping("{userId}")
    public void deleteById(@PathVariable int userId) {
        userService.deleteById(userId);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<Film> getRecommendations(@PathVariable int id) {
        return filmService.getRecommendations(id);
    }
}