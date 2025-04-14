package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j(topic = "TRACE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    //сообщения для логирования и исключений
    private static final String LOG_POST_REQUEST = "Обработка Post-запроса...";
    private static final String LOG_DELETE_REQUEST = "Обработка Del-запроса...";
    private static final String LOG_GET_REQUEST = "Обработка Get-запроса...";
    private static final String ERROR_FRIEND_ALREADY_ADDED = "Пользователь с id %d уже добавлен в друзья";
    private static final String ERROR_USER_NOT_FOUND = "Пользователь с данным идентификатором отсутствует в базе";

    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addFriend(Long idUser, Long idFriend) {
        log.info(LOG_POST_REQUEST);

        validateUserExists(idUser);
        validateUserExists(idFriend);

        if (isFriend(idUser, idFriend)) {
            logAndThrowConditionsNotMetException(String.format(ERROR_FRIEND_ALREADY_ADDED, idFriend));
        }

        userStorage.addFriendSql(idUser, idFriend);
        return userStorage.findById(idUser);
    }

    @Override
    public User delFriend(Long idUser, Long idFriend) {
        log.info(LOG_DELETE_REQUEST);

        validateUserExists(idUser);
        validateUserExists(idFriend);

        userStorage.delFriends(idUser, idFriend);
        return userStorage.findById(idUser);
    }

    @Override
    public Set<User> findJointFriends(Long idUser, Long idFriend) {
        log.info(LOG_GET_REQUEST);

        validateUserExists(idUser);
        validateUserExists(idFriend);

        String sqlSelectJointFriends = "SELECT f1.friendId AS jointFriendId " +
                "FROM friends f1 " +
                "JOIN friends f2 ON f1.friendId = f2.friendId " +
                "WHERE f1.userId = ? AND f2.userId = ?";

        Set<Long> jointFriendIds = new HashSet<>(jdbcTemplate.queryForList(
                sqlSelectJointFriends, Long.class, idUser, idFriend));

        Set<User> result = new HashSet<>();
        for (Long friendId : jointFriendIds) {
            result.add(userStorage.findById(friendId));
        }
        return result;
    }

    @Override
    public Set<User> findAllFriends(Long idUser) {
        log.info(LOG_GET_REQUEST);

        validateUserExists(idUser);

        Set<User> result = new HashSet<>();
        for (Long friendId : userStorage.selectFriends(idUser)) {
            result.add(userStorage.findById(friendId));
        }
        return result;
    }

    private void validateUserExists(Long userId) {
        if (userStorage.findById(userId) == null) {
            logAndThrowNotFoundException(userId.toString());
        }
    }

    private boolean isFriend(Long idUser, Long idFriend) {
        assert userStorage.sqlFriends() != null;
        return userStorage.sqlFriends().getOrDefault(idUser, new HashSet<>()).contains(idFriend);
    }

    private void logAndThrowConditionsNotMetException(String message) {
        log.error("Exception", new ConditionsNotMetException(message));
        throw new ConditionsNotMetException(message);
    }

    private void logAndThrowNotFoundException(String value) {
        log.error("Exception", new NotFoundException(UserServiceImpl.ERROR_USER_NOT_FOUND));
        throw new NotFoundException(UserServiceImpl.ERROR_USER_NOT_FOUND);
    }
}