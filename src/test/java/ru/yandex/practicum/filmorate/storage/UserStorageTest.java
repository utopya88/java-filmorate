package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class UserStorageTest {

    private final UserService userService;

    private final User user1 = User.builder()
            .email("1@ya.ru")
            .login("login1")
            .name("Name1")
            .birthday(LocalDate.parse("1967-03-01"))
            .build();

    private final User user2 = User.builder()
            .email("2@ya.ru")
            .login("login2")
            .name("Name2")
            .birthday(LocalDate.parse("1967-03-01"))
            .build();

    private final User user3 = User.builder()
            .email("3@ya.ru")
            .login("login3")
            .name("Name3")
            .birthday(LocalDate.parse("1967-03-01"))
            .build();

    @Test
    public void createUserTest() {
        userService.create(user1);
        assertEquals(1, userService.findUserById(1).getId());
        assertEquals(3, userService.findAll().size());
        user1.setId(1L);
        user1.setName("Test");
        userService.update(user1);
        assertEquals("Test", userService.findUserById(1).getName());
    }

    @Test
    public void getAllUsersTest() {
        userService.create(user2);
        userService.create(user3);
        Collection<User> dbUsers = userService.findAll();
        assertEquals(2, dbUsers.size());
    }

    @Test
    public void deleteUserTest() {
        Collection<User> beforeDelete = userService.findAll();
        userService.deleteById(1);
        Collection<User> afterDelete = userService.findAll();
        assertEquals(beforeDelete.size() - 1, afterDelete.size());
    }
}