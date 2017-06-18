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
    private DAO DAO;

    @Override
    public Optional<Survey> getSurveyById( Integer id, DeserializeSurveyOptions... surveyOptions ){
        try{
            return DAO.findSurvey( id , surveyOptions );
        }catch( IOException e ){
            return Optional.empty();
        }
    }


    @Override
    public Optional<User> getMadeUser( Integer id, DeserializeUserOptions... options ){
        return Optional.of( getSurveyById( id , DeserializeSurveyOptions.CREATOR ).orElseThrow( () -> new SurveyNotFoundException( id ) )
                .getCreator() );
    }

    @Override
    public List<Survey> getTop( String sortBy, Integer limit, DeserializeSurveyOptions... options ){
        try{
            return DAO.listSurveysByPredicate( path -> true , SurveysSort.valueOf( sortBy.toUpperCase() ) , limit , options );
        }catch( IOException e ){
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Category> getCategoryByName( String name, Boolean downloadSurveys, SurveysSort surveysSort,
                                                 Integer limit ){
        try{
            return DAO.findCategory( name, downloadSurveys, surveysSort, limit );
        }catch( IOException e ){
            return Optional.empty();
        }
    }

    @Override
    public List<Category> getCategories( Boolean downloadSurveys , SurveysSort surveysSort , Integer limit ){
        try{
            return DAO.listCategories( downloadSurveys, surveysSort, limit );
        }catch( IOException e ){
            return new ArrayList<>();
        }
    }
}
