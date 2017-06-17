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
        // TODO: 16.06.17  
        return null;
    }


    @Override
    public Optional<User> getMadeUser( Integer id, DeserializeUserOptions... options ){
        // TODO: 16.06.17
        return null;
    }

    @Override
    public List<Survey> getTop( String sortBy, Integer limit, DeserializeSurveyOptions... options ){
        // TODO: 16.06.17
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
}
