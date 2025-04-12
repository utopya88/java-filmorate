package ru.yandex.practicum.filmorate.storage.feed.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Feed> getFeedByUserId(long userId) {
        String sqlQuery = "SELECT * FROM FEED WHERE USER_ID = ? ORDER BY TIME_STAMP ASC";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFeed, userId);
    }

    @Override
    public void addFeed(long entityId, long userId, long timeStamp,
                        EventType eventType, Operation operation) {
        String sqlQuery = "INSERT INTO FEED(ENTITY_ID, USER_ID, TIME_STAMP, EVENT_TYPE, OPERATION) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery, entityId, userId, timeStamp, eventType.toString(), operation.toString());
    }

    private Feed mapRowToFeed(ResultSet resultSet, int rowNum) throws SQLException {
        return Feed.builder()
                .eventId(resultSet.getInt("EVENT_ID"))
                .entityId(resultSet.getInt("ENTITY_ID"))
                .userId(resultSet.getInt("USER_ID"))
                .timestamp(resultSet.getLong("TIME_STAMP"))
                .eventType(EventType.valueOf(resultSet.getString("EVENT_TYPE")))
                .operation(Operation.valueOf(resultSet.getString("OPERATION")))
                .build();
    }
}