package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.Set;

@Data
public class User {
    private Integer id;
    @NonNull
    private String email;
    @NonNull
    private String login;
    private String name;
    @NonNull
    LocalDate birthday;
    private Set<Integer> friends;

    public Set<Integer> getFriends() {
        return friends;
    }
}
