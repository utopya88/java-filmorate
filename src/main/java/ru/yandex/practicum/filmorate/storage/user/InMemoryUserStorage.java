package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int id;

    @Override
    public List<User> get() {
        log.info("Текущее количество пользователей: {}", users.size());
        return users.values().parallelStream().collect(Collectors.toList());
    }

    @Override
    public User create(User user) {
        if (validate(user)) {
            user.setId(++this.id);
            if (user.getFriends() == null) {
                user.setFriends(new HashMap<>());
            }
            users.put(user.getId(), user);
            log.info("Добавлен новый пользователь: {}", user);
        }
        return user;
    }

    @Override
    public User update(User user) {
        validate(user);
        int userId = user.getId();
        if (!users.containsKey(userId)) {
            log.info("Не найден пользователь в списке с id: {}", userId);
            throw new NotFoundException();
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashMap<>());
        }
        users.put(userId, user);
        log.info("Обновлены данные пользователя с id {}. Новые данные: {}", userId, user);
        return user;
    }

    @Override
    public User getUserById(Integer userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new NotFoundException();
        }
    }

    public static boolean validate(User user) {
       validateEmail(user.getEmail());
       validateLogin(user.getLogin());
       user = validateUserName(user);
       validateBirthday(user.getBirthday());
       return true;
    }


    private static User validateUserName(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

    private static void validateEmail(String email) {
        if ((!email.contains("@")) || email.isBlank()) {
            throw new ValidationException("Ошибка валидации. Неверный email формат.");
        }
    }

    private static void validateLogin(String login) {
        if (login.isEmpty() || login.isBlank() || login.contains(" ")) {
            throw new ValidationException("Ошибка валидации. Логин не может быть пустым или с пробелами");
        }
    }

    private static void validateBirthday(LocalDate birthday) {
        if (birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Ошибка валидации. Вы из будущего?)");
        }
    }









    @Override
    public List<User> addToFriends(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        update(user);
        update(friend);
        Set<Integer> friendsId = user.getFriends().keySet();
        List<User> friends = new ArrayList<>();
        for (Integer id : friendsId) {
            friends.add(getUserById(id));
        }
        return friends;
    }

    @Override
    public void deleteFromFriends(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        user.deleteFromFriends(friendId);
    }

    @Override
    public List<User> getFriends(Integer userId) {
        List<User> friends = new ArrayList<>();
        User user = getUserById(userId);
        Set<Integer> friendsId = user.getFriends().keySet();
        for (Integer friendId : friendsId) {
            friends.add(getUserById(friendId));
        }
        return friends;
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        List<User> friends = new ArrayList<>();
        User user = getUserById(userId);
        Set<Integer> userFriendsId = user.getFriends().keySet();
        User friend = getUserById(friendId);
        Set<Integer> friendsId = friend.getFriends().keySet();
        List<Integer> commonId = userFriendsId.stream().filter(friendsId::contains).collect(Collectors.toList());
        for (Integer id : commonId) {
            friends.add(getUserById(id));
        }
        return friends;
    }
}
