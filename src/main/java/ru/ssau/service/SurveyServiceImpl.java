package ru.ssau.service;

import org.springframework.stereotype.Service;
import ru.ssau.domain.Category;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;
import ru.ssau.exceptions.SurveyNotFoundException;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SurveyServiceImpl implements SurveyService{
    private static Map<Integer, Survey>  surveys;
    private static Map<String, Category> categoriesMap;

    static{
        surveys = Stream.iterate( new Survey( 0 ), new UnaryOperator<Survey>(){
            int i = 0;

            @Override
            public Survey apply( Survey t ){
                return new Survey( ++i );
            }
        } ).limit( 20 ).collect( Collectors.toMap( Survey::getId, p -> p ) );
        categoriesMap = new HashMap<>();
        categoriesMap.put( "category1", new Category( "category1", surveys.values().stream().limit( 5 ).collect(
                Collectors.toList() ) ) );
        categoriesMap.put( "category2", new Category( "category2",
                                                      surveys.values().stream().skip( 5 ).limit( 5 ).collect(
                                                              Collectors.toList() ) ) );
        categoriesMap.put( "category3", new Category( "category3",
                                                      surveys.values().stream().skip( 10 ).limit( 5 ).collect(
                                                              Collectors.toList() ) ) );
        categoriesMap.put( "category4", new Category( "category4",
                                                      surveys.values().stream().skip( 15 ).limit( 5 ).collect(
                                                              Collectors.toList() ) ) );
    }

    @Override
    public Optional<Survey> getSurveyById( Integer id ){
        if( !surveys.containsKey( id ) )
            return Optional.empty();
        return Optional.of( surveys.get( id ) );
    }

    @Override
    public Optional<User> getMadeUser( Integer id ){
        return Optional.ofNullable(
                getSurveyById( id ).orElseThrow( () -> new SurveyNotFoundException( id ) ).getMadeByUser() );
    }

    @Override
    public List<Survey> getTop(){
        return surveys.values().stream().sorted( Comparator.comparingInt( Survey::getUsersDone ) ).collect(
                Collectors.toList() );
    }

    @Override
    public Optional<Category> getCategoryByName( String name ){
        if( !categoriesMap.containsKey( name ) )
            return Optional.empty();
        return Optional.of( categoriesMap.get( name ) );
    }

    @Override
    public List<Category> getCategories(){
        return categoriesMap.values().stream().collect( Collectors.toList() );
    }
}
