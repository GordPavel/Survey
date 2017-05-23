package ru.ssau.service;

import org.springframework.stereotype.Service;
import ru.ssau.domain.Category;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;
import ru.ssau.exceptions.SurveyNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        } ).limit( 20 ).sorted( ( a, b ) -> b.getUsersDone() - a.getId() ).collect(
                Collectors.toMap( Survey::getId, p -> p ) );
        categoriesMap = new HashMap<>();
//        Добавление анкет в категории и сортивроке по отвеченным пользователям
        for( int i = 0, j = 1 ; j <= 4 ; i += 5, j++ )
            categoriesMap.put( "category" + j, new Category( "category" + j,
                                                             surveys.values().stream().skip( i ).limit( 5 ).collect(
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
        return surveys.values().stream().sorted( ( a, b ) -> b.getUsersDone() - a.getUsersDone() ).collect(
                Collectors.toList() );
    }

    @Override
    public Optional<Category> getCategoryByName( String name ){
        if( !categoriesMap.containsKey( name ) )
            return Optional.empty();
        categoriesMap.get( name ).getSurveys().sort( ( a, b ) -> b.getUsersDone() - a.getUsersDone() );
        return Optional.of( categoriesMap.get( name ) );
    }

    @Override
    public List<Category> getCategories(){
//        сначала преобразуем в лист RatingTopics, чтобы можно было сортировать по отвеченным пользователям,
//        затем снова преобразуем в лист Category, сортируем внутри каждой категории все анкеты и собираем в лист
        return categoriesMap.values().stream()
                .map( category -> new RatingTopics( category.getSurveys().stream()
                                                            .mapToInt( Survey::getUsersDone )
                                                            .reduce( ( a, b ) -> a + b ).getAsInt(),
                                                            category ) )
                .sorted( ( a, b ) -> Integer.compare( b.getUsersDone() , a.getUsersDone() ) )
                .map( ratingTopics -> ratingTopics.category )
                .peek( category ->
                               category.getSurveys().sort( ( a, b ) -> Integer.compare( b.getUsersDone() , a.getUsersDone() )  ) )
                .collect( Collectors.toList() );
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
