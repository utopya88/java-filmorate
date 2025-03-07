package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.FindObjectException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;


    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public ArrayList<User> viewInterFriends(Integer idFriendOne, Integer idFriendTwo) {
        Set<Integer> firstFriendSet = userStorage.getUserById(idFriendOne).getFriends();
        Set<Integer> secondFriendSet = userStorage.getUserById(idFriendTwo).getFriends();
        firstFriendSet.retainAll(secondFriendSet);
        ArrayList<User> interFriends = new ArrayList<>();
        for (Integer i: firstFriendSet) {
            interFriends.add(userStorage.getUserById(i));
        }
        log.trace("Вернули общий список друзей у двух пользователей");
        return interFriends;
    }

    public List<User> returnFriendsList(Integer id) {
        List<User> friend = new ArrayList<>();
        for (Integer i: userStorage.getUserById(id).getFriends()) {
            friend.add(userStorage.getUserById(i));
        }
        return friend;
    }

    public boolean addFriend(Integer id, Integer friendId) {
         userStorage.getUserById(id).getFriends().add(friendId);
         userStorage.getUserById(friendId).getFriends().add(id);
         log.trace("Добавили друзей друг другу");
         return true;
    }

    public boolean deleteFriend(Integer id, Integer friendId) {
        userStorage.getUserById(id).getFriends().remove(friendId);
        userStorage.getUserById(friendId).getFriends().remove(id);
        log.trace("Удалили друзей у друг друга");
        return true;
    }

    public User create(User user) {
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
