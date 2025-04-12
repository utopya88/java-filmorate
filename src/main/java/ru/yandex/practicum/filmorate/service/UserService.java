package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.model.enums.EventType.FRIEND;
import static ru.yandex.practicum.filmorate.model.enums.Operation.ADD;
import static ru.yandex.practicum.filmorate.model.enums.Operation.REMOVE;

@Slf4j
@Service
@RequiredArgsConstructor

public class UserService {

    private final UserStorage userStorage;
    private final FeedService feedService;

    public Collection<User> findAll() {
        log.info("Выводим список всех пользователей");
        return userStorage.findAll();
    }

    public User create(User user) {

        log.info("Проверяем user в валидаторах");
        validateLogin(user);
        user = validateName(user);
        User userFromCreator = userCreator(user);
        log.info("Добавляем объект в коллекцию");
        return userStorage.save(userFromCreator);
    }

    public User update(User user) {
        log.info("Проверяем user в валидаторах");
        validateLogin(user);
        user = validateName(user);

        User userFromCreator = userCreator(user);
        log.info("Обновляем объект в коллекции");

        findUserById(userFromCreator.getId());
        return userStorage.update(userFromCreator);
    }

    public User userCreator(User user) {
        log.info("Создаем объект");
        User userFromBuilder = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();
        log.info("Объект User создан, имя : '{}'", userFromBuilder.getName());
        return userFromBuilder;
    }

    public void validateLogin(User user) {
        if (user.getLogin().contains(" ")) {
            log.info("login пользователя '{}' ", user.getLogin());
            throw new ValidationException("Пробел в login недопустим!");
        }
    }

    public User validateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Присваиваем поле login '{}' для поля name '{}' ", user.getLogin(), user.getName());
            user.setName(user.getLogin());
        }
        return user;
    }

    public User findUserById(long id) {
        return userStorage.findUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден!"));
    }

    public void addFriend(long id, long friendId) {
        userStorage.addFriend(findUserById(id), findUserById(friendId));
        feedService.addFeed(friendId, id, FRIEND, ADD);
    }

    public void deleteFriend(long id, long friendId) {
        userStorage.deleteFriend(findUserById(id), findUserById(friendId));
        feedService.addFeed(friendId, id, FRIEND, REMOVE);
    }

    public Collection<User> getFriendsFromUser(long userId) {
        return userStorage.getFriendsFromUser(findUserById(userId).getId());
    }

    public Collection<User> getCommonFriendsFromUser(long id, long otherId) {
        return userStorage.getCommonFriendsFromUser(findUserById(id).getId(), findUserById(otherId).getId());
    }

    public Collection<Feed> getFeedByUserId(long id) {
        findUserById(id);
        return feedService.getFeedByUserId(id);
    }

    public void deleteById(long userId) {

        userStorage.deleteById(userId);
    }
}