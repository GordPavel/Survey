package ru.ssau.service;

import org.springframework.stereotype.Service;
import ru.ssau.domain.Survey;
import ru.ssau.domain.Topic;
import ru.ssau.domain.User;
import ru.ssau.exceptions.SurveyNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SurveyServiceImpl implements SurveyService{

    // TODO: 01.06.17 Работа с БД

    @Override
    public Optional<Survey> getSurveyById( Integer id ){
        return null;
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
