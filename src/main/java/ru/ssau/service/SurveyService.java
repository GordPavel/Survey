package ru.ssau.service;

import ru.ssau.DAO.enums.DeserializeSurveyOptions;
import ru.ssau.DAO.enums.DeserializeUserOptions;
import ru.ssau.DAO.enums.SurveysSort;
import ru.ssau.domain.Category;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;

import java.util.List;
import java.util.Optional;

public interface SurveyService{

    List<Survey> getTop( String sortBy, Integer limit, DeserializeSurveyOptions... options );

    Optional<Survey> getSurveyById( Integer id, DeserializeSurveyOptions... surveyOptions );

    Optional<User> getMadeUser( Integer id, DeserializeUserOptions... options );

    Optional<Category> getCategoryByName( String name, Boolean downloadSurveys, SurveysSort surveysSort,
                                          Integer limit );

    List<Category> getCategories( Boolean downloadSurveys , SurveysSort surveysSort , Integer limit );
}
