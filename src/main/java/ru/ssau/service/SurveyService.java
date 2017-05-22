package ru.ssau.service;

import ru.ssau.domain.Category;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;

import java.util.List;
import java.util.Optional;

public interface SurveyService{

    List<Survey> getTop();

    Optional<Survey> getSurveyById( Integer id );

    Optional<User> getMadeUser( Integer id );

    Optional<Category> getCategoryByName( String name );

    List<Category> getCategories();
}
