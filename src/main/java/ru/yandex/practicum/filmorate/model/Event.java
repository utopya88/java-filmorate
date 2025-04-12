package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Event {

    @NonNull
    private Long eventId;
    @NonNull
    private Long userId;
    @NonNull
    private Long timestamp;
    private String eventType;
    private String operation;
    @NonNull
    private Long entityId;

}