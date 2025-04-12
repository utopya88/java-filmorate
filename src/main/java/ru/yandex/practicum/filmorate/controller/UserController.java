package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserInterface;
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
    private final UserInterface userInterface;

    /**
     * получить список всех пользователей
     *
     * @return список всех пользователей
     */
    @GetMapping
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    /**
     * получить пользователя по его ид
     *
     * @param id идентификатор пользователя
     * @return объект пользователя
     */
    @GetMapping(USER_ID_PATH)
    public User findById(@PathVariable("id") Long id) {
        return userStorage.findById(id);
    }

    /**
     * создать нового пользователя
     *
     * @param user объект пользователя
     * @return созданный пользователь
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    /**
     * обновить данные пользователя
     *
     * @param newUser объект пользователя с новыми данными
     * @return обновленный пользователь
     */
    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return userStorage.update(newUser);
    }

    /**
     * добавить друга пользователю
     *
     * @param id       идентификатор пользователя
     * @param friendId идентификатор друга
     * @return пользователь с обновленным списком друзей
     */
    @PutMapping(FRIEND_ID_PATH)
    public User addFriend(@Valid @PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        return userInterface.addFriend(id, friendId);
    }

    /**
     * удалить друга у пользователя
     *
     * @param id       идентификатор пользователя
     * @param friendId идентификатор друга
     * @return пользователь с обновленным списком друзей
     */
    @DeleteMapping(FRIEND_ID_PATH)
    public User delFriend(@Valid @PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        return userInterface.delFriend(id, friendId);
    }

    /**
     * получить список общих друзей двух пользователей
     *
     * @param id      идентификатор первого пользователя
     * @param otherId идентификатор второго пользователя
     * @return список общих друзей
     */
    @GetMapping(COMMON_FRIENDS_PATH)
    public Set<User> findJointFriends(@Valid @PathVariable("id") Long id, @PathVariable("otherId") Long otherId) {
        return userInterface.findJointFriends(id, otherId);
    }

    /**
     * получить список друзей пользователя
     *
     * @param id идентификатор пользователя
     * @return список друзей
     */
    @GetMapping(FRIENDS_PATH)
    public Set<User> findAllFriends(@Valid @PathVariable("id") Long id) {
        return userInterface.findAllFriends(id);
    }
}