package ru.yandex.practicum.filmorate.model;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ValidationErrorResponse {

    private final List<ErrorResponse> errorResponses;
}