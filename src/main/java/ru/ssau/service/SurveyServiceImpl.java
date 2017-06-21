package ru.ssau.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ssau.DAO.DAO;
import ru.ssau.DAO.enums.DeserializeSurveyOptions;
import ru.ssau.DAO.enums.DeserializeUserOptions;
import ru.ssau.DAO.enums.SurveysSort;
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
    private DAO dao;

    @Override
    public Optional<Survey> getSurveyById( Integer id,
                                           DeserializeSurveyOptions... surveyOptions ) throws InterruptedException{
        try{
            dao.beginTransaction();
            Optional<Survey> survey = dao.findSurvey( id, surveyOptions );
            dao.endTransaction();
            return survey;
        }catch( IOException e ){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Integer> saveSurvey( Survey survey ) throws InterruptedException{
        try{
            dao.beginTransaction();
            Integer id = dao.saveNewSurvey( survey );
            dao.endTransaction();
            return Optional.of( id );
        }catch( IOException e ){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void deleteSurvey( Integer id ) throws InterruptedException{
        try{
            dao.beginTransaction();
            dao.deleteSurvey( id );
            dao.endTransaction();
        }catch( IOException e ){
            e.printStackTrace();
        }
    }

    @Override
    public Optional<User> getMadeUser( Integer id ) throws InterruptedException{
        dao.beginTransaction();
        Optional<User> user = Optional.of( getSurveyById( id, DeserializeSurveyOptions.CREATOR ).orElseThrow(
                () -> new SurveyNotFoundException( id ) ).getCreator() );
        dao.endTransaction();
        return user;
    }

    @Override
    public List<Survey> getTop( String sortBy, Integer limit, DeserializeSurveyOptions... options ){
        try{
            dao.beginTransaction();
            List<Survey> surveys = dao.listAllSurveys( SurveysSort.valueOf( sortBy.toUpperCase() ) , limit , options );
            dao.endTransaction();
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
            dao.beginTransaction();
            Optional<Category> category = dao.findCategory( name, downloadSurveys, surveysSort , limit );
            dao.endTransaction();
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
            dao.beginTransaction();
            List<Category> categories = dao.listAllCategories( downloadSurveys, surveysSort, limit );
            dao.endTransaction();
            return categories;
        }catch( IOException e ){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
