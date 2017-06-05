package ru.ssau.service;

import ru.ssau.domain.Survey;
import ru.ssau.domain.Topic;

import java.util.List;
import java.util.Optional;

public interface SurveyService{

    List<Survey> getTop( String orderedBy, Integer limit );

    Optional<Survey> getSurveyById( Integer id );

    void saveSurvey( Survey survey );

    void deleteSurvey( Survey survey );

    List<Topic> topics( String orderedBy, Integer limit );

    Optional<Topic> getTopicByName( String name );
}
