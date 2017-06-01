package ru.ssau.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ssau.DAO.SurveyDAO;
import ru.ssau.domain.Survey;
import ru.ssau.domain.Topic;
import ru.ssau.domain.User;

import java.util.List;
import java.util.Optional;

@Service
public class SurveyServiceImpl implements SurveyService{


    @Autowired
    private SurveyDAO surveyDAO;

    @Override
    public Optional<Survey> getSurveyById( Integer id ){
        return Optional.of( surveyDAO.getById( id ) );
    }

    @Override
    public void saveSurvey( Survey survey ){

    }

    @Override
    public Optional<User> getMadeUser( Integer id ){
        return null;
    }

    @Override
    public List<Survey> getTop(){
        return null;
    }

    @Override
    public Optional<Topic> getCategoryByName( String name ){
        return null;
    }

    @Override
    public List<Topic> getCategories(){
        return null;
    }
}
