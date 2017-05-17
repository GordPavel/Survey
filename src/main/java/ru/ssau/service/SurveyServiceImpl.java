package ru.ssau.service;

import org.springframework.stereotype.Service;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;
import ru.ssau.exceptions.SurveyNotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SurveyServiceImpl implements SurveyService{
    private static List<Survey> surveys;

    static{
        surveys = Stream.iterate( new Survey( 0 ), new UnaryOperator<Survey>(){
            int i = 0;
            @Override
            public Survey apply( Survey t ){
                return new Survey( ++i );
            }
        } ).limit( 20 ).collect( Collectors.toList() );
    }

    @Override
    public Optional<Survey> getSurveyById( Integer id ){
        if( id == 0 )
            return Optional.empty();
        return Optional.of( new Survey( id ) );
    }

    @Override
    public Optional<User> getMadeUser( Integer id ){
        return Optional.of(
                getSurveyById( id ).orElseThrow( () -> new SurveyNotFoundException( id ) ).getMadeByUser() );
    }

    @Override
    public List<Survey> getTop(){
        return surveys.stream().sorted( Comparator.comparing( Survey::getUsersDone ) ).collect( Collectors.toList() );
    }
}
