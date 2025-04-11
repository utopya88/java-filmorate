package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;


import ru.yandex.practicum.filmorate.exception.FindObjectException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public User getUserById(Integer id) {
        if (!userStorage.isFindUserById(id)) {
            throw new FindObjectException("Пользователь с таким идентификатором не найден");
        }
        return userStorage.getUserById(id).get();
    }

    public List<User> viewInterFriends(Integer idFriendOne, Integer idFriendTwo) {
        if (!userStorage.isFindUserById(idFriendOne) || !userStorage.isFindUserById(idFriendTwo)) {
            throw new FindObjectException("Пользователь с таким идентификатором не найден");
        }
        return returnFriendsList(idFriendOne).stream()
                .filter(f -> returnFriendsList(idFriendTwo).contains(f))
                .collect(Collectors.toList());
    }

    public List<User> returnFriendsList(Integer id) {
        List<User> friend = new ArrayList<>();
        for (Integer i: getUserById(id).getFriends()) {
            friend.add(getUserById(i));
        }
        return friend;
    }

    public boolean addFriend(Integer id, Integer friendId) {
        if (!userStorage.isFindUserById(id) || !userStorage.isFindUserById(friendId)) {
            throw new FindObjectException("Не найден идентификатор пользователя или его друга");
        }
         getUserById(id).getFriends().add(friendId);
         getUserById(friendId).getFriends().add(id);
         log.trace("Добавили друзей друг другу");
         return true;
    }

    public boolean deleteFriend(Integer id, Integer friendId) {
        if (!userStorage.isFindUserById(id) || !userStorage.isFindUserById(friendId)) {
            throw new FindObjectException("Не найден идентификатор пользователя или его друга");
        }
        getUserById(id).getFriends().remove(friendId);
        getUserById(friendId).getFriends().remove(id);
        log.trace("Удалили друзей у друг друга");
        return true;
    }

    public User create(User user) {
        if (userStorage.findAll().contains(user)) {
            throw new FindObjectException("Такой пользователь уже существуют");
        }
        return userStorage.create(user).get();
    }

    public User update(User user) {
        if (!userStorage.isFindUserById(user.getId())) {
            throw new FindObjectException("Не найден обьект для обновления");
        }
        return userStorage.update(user).get();
    }

    public ArrayList<User> findAllUsers() {
        return userStorage.findAll();
    }
}
