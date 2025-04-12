package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User save(User user);

    User update(User user);

    Collection<User> findAll();

    Optional<User> findUserById(long id);

    void addFriend(User user, User friend);

    boolean deleteFriend(User user, User friend);

    Collection<User> getFriendsFromUser(long id);

    Collection<User> getCommonFriendsFromUser(long id, long otherId);

    void deleteById(long userId);

    long findUserWithCommonLikes(long userId);
}