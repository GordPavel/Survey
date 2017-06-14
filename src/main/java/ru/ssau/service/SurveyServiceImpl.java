package ru.ssau.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ssau.DAO.survey.SurveyDAO;
import ru.ssau.domain.Category;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class SurveyServiceImpl implements SurveyService{


    @Autowired
    private SurveyDAO surveyDAO;

    @Override
    public Optional<Survey> getSurveyById( Integer id ){
        try{
            surveyDAO.beginTransaction();
        }catch( InterruptedException e ){
            e.printStackTrace();
        }
        Optional<Survey> survey;
        try{
            survey = Optional.of( surveyDAO.findById( id ) );
        }catch( IOException e ){
            survey = Optional.empty();
        }
        surveyDAO.endTransaction();
        return survey;
    }


    @Override
    public Optional<User> getMadeUser( Integer id ){
        // TODO: 13.06.17
        return null;
    }

    @Override
    public List<Survey> getTop(){
        // TODO: 13.06.17
        return null;
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

    class RatingTopics{
        Integer  usersDone;
        Category category;

        RatingTopics( Integer usersDone, Category category ){
            this.usersDone = usersDone;
            this.category = category;
        }

        Integer getUsersDone(){
            return usersDone;
        }
    }
}
