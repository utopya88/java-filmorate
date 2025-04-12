package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.util.Collection;

public interface FeedStorage {

    Collection<Feed> getFeedByUserId(long userId);

    void addFeed(long entityId, long userId, long timeStamp, EventType eventType, Operation operation);
}
