package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Feed {

    private long eventId;
    private long entityId;
    private long userId;
    private long timestamp;
    private EventType eventType;
    private Operation operation;

    @Override
    public String toString() {
        return "Feed{" +
                "eventId=" + eventId +
                ", entityId=" + entityId +
                ", userId=" + userId +
                ", timestamp=" + timestamp +
                ", eventType=" + eventType +
                ", operation=" + operation +
                '}';
    }
}
