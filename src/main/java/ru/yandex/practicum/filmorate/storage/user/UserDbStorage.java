package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.FindObjectException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

@Primary
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<User> create(User user) {
        String sqlQuery = "INSERT INTO users(user_name, email, login, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getLogin());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());
        return Optional.of(user);
    }

    @Override
    public Optional<User> update(User user) {
        String sqlQuery = "UPDATE users SET user_name = ?, email = ?, login = ?, birthday = ?" +
                " WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getName(), user.getEmail(), user.getLogin(),
                user.getBirthday(), user.getId());
        return Optional.of(user);
    }

    public boolean delete(User user) {
        String sqlQuery = "DELETE FROM users WHERE user_id = ?";
        return jdbcTemplate.update(sqlQuery, user.getId()) > 0;
    }

    public boolean deleteUserById(Integer userId) {
        String sqlQuery = "DELETE FROM users WHERE user_id = ?";
        return jdbcTemplate.update(sqlQuery, userId) > 0;
    }

    @Override
    public ArrayList<User> findAll() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    @Override
    public Optional<User> getUserById(Integer userId) {
        String sqlQuery = "select * from users where user_id = ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sqlQuery, this::makeUser, userId));
        } catch (EmptyResultDataAccessException e) {
            log.warn("Пользователь № {} не найден", userId);
            throw new FindObjectException(String.format("Пользователь c идентификатором № %d не найден", userId));
        }
    }

    @Override
    public boolean isFindUserById(Integer userId) {
        String sqlQuery = "select exists(select 1 from users where user_id = ?)";
        if (jdbcTemplate.queryForObject(sqlQuery, Boolean.class, userId)) {
            return true;
        }
        log.warn("Пользователь с идентификатором № {} не найден", userId);
        throw new FindObjectException(String.format("Пользователь с идентификатором № %d не найден", userId));
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User(rs.getString("email"), rs.getString("login"),
                rs.getDate("birthday").toLocalDate());
        user.setId(rs.getInt("user_id"));
        user.setName(rs.getString("user_name"));
        return user;
    }

    public boolean addInFriends(User friendRequest, User friendResponse) {
        String sqlQuery = "merge into friends(request_friend_id,response_friend_id) values (?, ?)";
        return jdbcTemplate.update(sqlQuery, friendRequest.getId(), friendResponse.getId()) > 0;
    }

    public boolean deleteFromFriends(User friendRequest, User friendResponse) {
        String sqlQuery = "delete from friends " +
                "where request_friend_id = ? " +
                "and response_friend_id = ?;";
        return jdbcTemplate.update(sqlQuery, friendRequest.getId(), friendResponse.getId()) > 0;
    }

    public List<Long> findFriends(Integer id) {
        String sqlQuery = "select response_friend_id from friends " +
                "where request_friend_id = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getInt("response_friend_id"), id);
    }

}
