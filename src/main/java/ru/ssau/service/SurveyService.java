package ru.ssau.service;

import ru.ssau.DAO.survey.DeserializeSurveyOptions;
import ru.ssau.DAO.user.DeserializeUserOptions;
import ru.ssau.domain.Category;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;

import java.util.List;
import java.util.Optional;

public interface SurveyService{

    List<Survey> getTop( String sortBy, Integer limit, DeserializeSurveyOptions... options );

    Optional<Survey> getSurveyById( Integer id, DeserializeSurveyOptions[] surveyOptions , DeserializeUserOptions[] userOptions );

    Optional<User> getMadeUser( Integer id, DeserializeUserOptions... options );

    Optional<Category> getCategoryByName( String name );

    List<Category> getCategories();
}
