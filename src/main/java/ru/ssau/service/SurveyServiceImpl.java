package ru.ssau.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ssau.DAO.DatabaseUtils;
import ru.ssau.DAO.enums.DeserializeSurveyOptions;
import ru.ssau.DAO.enums.SurveysSort;
import ru.ssau.domain.Category;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;
import ru.ssau.exceptions.SurveyNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/***
 * class to connect survey requests and database
 */

@Service
public class SurveyServiceImpl implements SurveyService{

    @Autowired
    private DatabaseUtils databaseUtils;

    @Override
    public Optional<Survey> getSurveyById( Integer id,
                                           DeserializeSurveyOptions... surveyOptions ) throws InterruptedException{
        try{
            databaseUtils.beginTransaction();
            Optional<Survey> survey = databaseUtils.findSurvey( id, surveyOptions );
            databaseUtils.endTransaction();
            return survey;
        }catch( IOException e ){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Integer> saveSurvey( Survey survey ) throws InterruptedException{
        try{
            databaseUtils.beginTransaction();
            Integer id = databaseUtils.saveNewSurvey( survey );
            databaseUtils.endTransaction();
            return Optional.of( id );
        }catch( IOException e ){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void deleteSurvey( Integer id ) throws InterruptedException{
        try{
            databaseUtils.beginTransaction();
            databaseUtils.deleteSurvey( id );
            databaseUtils.endTransaction();
        }catch( IOException e ){
            e.printStackTrace();
        }
    }

    @Override
    public Optional<User> getMadeUser( Integer id ) throws InterruptedException{
        databaseUtils.beginTransaction();
        Optional<User> user = Optional.of( getSurveyById( id, DeserializeSurveyOptions.CREATOR ).orElseThrow(
                () -> new SurveyNotFoundException( id ) ).getCreator() );
        databaseUtils.endTransaction();
        return user;
    }

    @Override
    public List<Survey> getTop( String sortBy, Integer limit, DeserializeSurveyOptions... options ){
        try{
            databaseUtils.beginTransaction();
            List<Survey> surveys = databaseUtils.listAllSurveys( SurveysSort.valueOf( sortBy.toUpperCase() ) , limit , options );
            databaseUtils.endTransaction();
            return surveys;
        }catch( IOException | InterruptedException e ){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Category> getCategoryByName( String name, Boolean downloadSurveys, SurveysSort surveysSort,
                                                 Integer limit ) throws InterruptedException{
        try{
            databaseUtils.beginTransaction();
            Optional<Category> category = databaseUtils.findCategory( name, downloadSurveys, surveysSort , limit );
            databaseUtils.endTransaction();
            return category;
        }catch( IOException e ){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Category> getCategories( Boolean downloadSurveys, SurveysSort surveysSort, Integer limit )
            throws InterruptedException{
        try{
            databaseUtils.beginTransaction();
            List<Category> categories = databaseUtils.listAllCategories( downloadSurveys, surveysSort, limit );
            databaseUtils.endTransaction();
            return categories;
        }catch( IOException e ){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
