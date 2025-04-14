package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface UserStorage {

    Collection<User> findAll();

    User findById(Long id);

    User create(User user);

    User update(User newUser);

    void addFriendSql(Long idUser, Long idFriend);

    Set<Long> selectFriends(Long idUser);

    Map<Long, Set<Long>> sqlFriends();

    void delFriends(Long idUser, Long idFriend);
}