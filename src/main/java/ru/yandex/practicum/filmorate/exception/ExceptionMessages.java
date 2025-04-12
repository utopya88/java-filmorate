package ru.yandex.practicum.filmorate.exception;

public class ExceptionMessages {

    public static final String FILM_ID_CANNOT_BE_NULL = "ид фильма не может быть равным null";
    public static final String FILM_NOT_FOUND = "фильм с таким ид не найден";
    public static final String FILM_NAME_CANNOT_BE_EMPTY = "Имя фильма не может быть пустым";
    public static final String FILM_DURATION_INVALID = "Длительность фильма должна быть положительным";
    public static final String FILM_DESCRIPTION_TOO_LONG = "Описание фильма не может быть длинней 200 символов";
    public static final String FILM_RELEASE_DATE_INVALID = "дата релиза фильма должна быть после 28 декабря 1985";
}