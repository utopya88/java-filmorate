package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<User> findUserById(long id) {
        String sqlQuery = "SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY " +
                "FROM USERS " +
                "WHERE USER_ID = ?";

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRows.next()) {
            User user = User.builder()
                    .id(userRows.getLong("USER_ID"))
                    .email(userRows.getString("EMAIL"))
                    .login(userRows.getString("LOGIN"))
                    .name(userRows.getString("NAME"))
                    .birthday(LocalDate.parse(userRows.getString("BIRTHDAY")))
                    .build();
            log.info("Найден пользователь {} с именем {} ", userRows.getLong("USER_ID"),
                    userRows.getString("LOGIN"));
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Collection<User> findAll() {
        String sqlQuery = "SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY " +
                "FROM USERS";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User save(User user) {
        String sqlQuery = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE USERS SET " +
                "EMAIL = ?, " +
                "LOGIN = ?, " +
                "NAME = ?, " +
                "BIRTHDAY = ? " +
                "WHERE USER_ID = ?";

        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public void addFriend(User user, User friend) {
        String sqlQuery = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery,
                user.getId(),
                friend.getId());
    }

    @Override
    public boolean deleteFriend(User user, User friend) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE FRIEND_ID = ?";

        return jdbcTemplate.update(sqlQuery,
                friend.getId()) > 0L;
    }

    @Override
    public Collection<User> getFriendsFromUser(long userId) {
        String sqlQuery = "SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY " +
                "FROM USERS " +
                "WHERE USER_ID IN " +
                "(SELECT FRIEND_ID FROM FRIENDS WHERE FRIENDS.USER_ID = ?)";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
    }

    @Override
    public Collection<User> getCommonFriendsFromUser(long id, long otherId) {
        String sqlQuery = "SELECT * FROM USERS " +
                "WHERE USER_ID IN (" +
                "SELECT F.FRIEND_ID " +
                "FROM FRIENDS AS F " +
                "JOIN FRIENDS AS FF ON F.FRIEND_ID = FF.FRIEND_ID " +
                "WHERE F.USER_ID = ?" +
                "AND FF.USER_ID = ?);";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, otherId);
    }

    @Override
    public void deleteById(long userId) {
        String sqlQuery = "DELETE FROM USERS WHERE USER_ID = ?";

        jdbcTemplate.update(sqlQuery, userId);
    }

    @Override
    public long findUserWithCommonLikes(long userWantsRecomId) {
        String sqlQuery = "SELECT fl2.user_id " +
                "FROM FILM_LIKES AS fl1, FILM_LIKES AS fl2 " +
                "WHERE fl1.film_id = fl2.film_id " +
                "AND fl1.USER_ID = ? AND fl1.USER_ID != fl2.USER_ID " +
                "GROUP BY fl1.user_id, fl2.user_id " +
                "ORDER BY count(*) desc limit 1";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, Long.class, userWantsRecomId);
        } catch (EmptyResultDataAccessException exception) {
            return userWantsRecomId;
        }
    }

    private User mapRowToUser(ResultSet resultSet, long rowNum) throws SQLException {

        return User.builder()
                .id(resultSet.getLong("USER_ID"))
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .name(resultSet.getString("NAME"))
                .birthday(resultSet.getObject("BIRTHDAY", LocalDate.class))
                .build();
    }
}
