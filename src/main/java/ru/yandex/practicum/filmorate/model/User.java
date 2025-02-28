package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Integer id;
    private String email;
    private String login;
    private String name;
    private Date birthday;
}
