package ru.ssau.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ssau.DAO.survey.DeserializeSurveyOptions;
import ru.ssau.DAO.survey.SurveyDAO;
import ru.ssau.DAO.survey.SurveysSort;
import ru.ssau.DAO.user.DeserializeUserOptions;
import ru.ssau.domain.Category;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;
import ru.ssau.exceptions.SurveyNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SurveyServiceImpl implements SurveyService{

    @Autowired
    private SurveyDAO surveyDAO;

    @Override
    public Optional<Survey> getSurveyById( Integer id, DeserializeSurveyOptions[] surveyOptions , DeserializeUserOptions[] userOptions ){
        try{
            surveyDAO.beginTransaction();
            Optional<Survey> survey = surveyDAO.listSurveysByPredicate(
                    path -> path.toString().substring( surveyDAO.getSurveyDirectoryNameLength() ).startsWith( id.toString() ),
                    SurveysSort.TIME, Integer.MAX_VALUE, surveyOptions ).stream().findFirst();
            surveyDAO.endTransaction();
            return survey;
        }catch( IOException | InterruptedException e ){
            return Optional.empty();
        }
    }


    @Override
    public Optional<User> getMadeUser( Integer id, DeserializeUserOptions... options ){
        try{
            surveyDAO.beginTransaction();
            Optional<User> user = Optional.ofNullable( surveyDAO.listSurveysByPredicate(
                    path -> path.toString().substring( surveyDAO.getSurveyDirectoryNameLength() ).startsWith( id.toString() ),
                    SurveysSort.TIME, 1, DeserializeSurveyOptions.CREATOR ).
                    stream().
                    findFirst().
                    orElseThrow( () -> new SurveyNotFoundException( id ) ).getCreator() );
            surveyDAO.endTransaction();
            return user;
        }catch( IOException | InterruptedException e ){
            return Optional.empty();
        }
    }

    @Override
    public List<Survey> getTop( String sortBy, Integer limit, DeserializeSurveyOptions... options ){
        try{
            surveyDAO.beginTransaction();
            List<Survey> surveys = surveyDAO.listSurveysByPredicate( survey -> true, SurveysSort.valueOf( sortBy ),
                                                                     limit, options );
            surveyDAO.endTransaction();
            return surveys;
        }catch( IOException | InterruptedException e ){
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Category> getCategoryByName( String name ){
        // TODO: 13.06.17
        return null;
    }

    @Override
    public List<Category> getCategories(){
        // TODO: 13.06.17
        return null;
    }
}
