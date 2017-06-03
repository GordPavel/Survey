package ru.ssau.DAO;

import ru.ssau.domain.Survey;
import ru.ssau.domain.User;

import java.util.List;

public interface SurveyDAO{

    void createNewSurvey( Survey survey );

    void removeSurvey( Survey survey );

    void updateSurvey( Survey survey );

    Survey getById( Integer integer );

    List<Survey> getAll();

    public User getMadeByUser( Integer id );
}
