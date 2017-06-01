package ru.ssau.DAO;

import ru.ssau.domain.Survey;

public interface SurveyDAO{

    Survey createNewSurvey( Survey survey );

    void removeSurvey( Survey survey );

    void updateSurvey( Survey survey );

    Survey getById( Integer integer );
}
