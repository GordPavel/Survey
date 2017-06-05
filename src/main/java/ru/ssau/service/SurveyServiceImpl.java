package ru.ssau.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.ssau.DAO.SurveyRepository;
import ru.ssau.DAO.TopicRepository;
import ru.ssau.DAO.UserAnswersRepository;
import ru.ssau.domain.Survey;
import ru.ssau.domain.Topic;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SurveyServiceImpl implements SurveyService{

    @Autowired
    private SurveyRepository      surveyRepository;
    @Qualifier( "userAnswersRepository" )
    @Autowired
    private UserAnswersRepository userAnswersRepository;
    @Qualifier( "topicRepository" )
    @Autowired
    private TopicRepository       topicRepository;

    @Override
    public List<Survey> getTop( String orderedBy, Integer limit ){
        if( orderedBy.equals( "users" ) ){
            return surveyRepository.findAll().stream().peek( survey -> survey.setUsersAnswered(
                    userAnswersRepository.getAllAnswersOnSurveyBySurveyId( survey.getId() ) ) ).sorted(
                    ( a, b ) -> Integer.compare( b.getUsersAnswered(), a.getUsersAnswered() ) ).limit( limit ).collect(
                    Collectors.toList() );
        }else return surveyRepository.getAllSortedByTime().stream().limit( limit ).collect( Collectors.toList() );
    }

    @Override
    public Optional<Survey> getSurveyById( Integer id ){
        Survey survey = surveyRepository.findOne( id );
        if( survey == null ) return Optional.empty();
        survey.setQuestions( surveyRepository.getQuestionsBySurveyId( id ) );
        survey.setAnswers( userAnswersRepository.getAllAnswersOnSurveyBySurveyId( id ) );
        return Optional.of( survey );
    }

    @Override
    public List<Topic> topics( String orderedBy, Integer limit ){
        List<Topic> topics = topicRepository.findAll();
        if( orderedBy.equals( "users" ) ) return topics.stream().map( topic -> new SurveyRating(
                topic.getSurveys().stream().mapToInt( Survey::getUsersAnswered ).reduce( ( a, b ) -> a + b ).getAsInt(),
                topic ) ).sorted( ( a, b ) -> Integer.compare( b.usersDone, a.usersDone ) ).map(
                surveyRating -> surveyRating.topic ).peek( topic -> topic.setSurveys(
                topic.getSurveys().stream().sorted(
                        ( a, b ) -> Integer.compare( b.getUsersAnswered(), a.getUsersAnswered() ) ).limit(
                        limit ).collect( Collectors.toList() ) ) ).collect( Collectors.toList() );
        else return topics.stream().map( topic -> new SurveyRating(
                topic.getSurveys().stream().mapToInt( Survey::getUsersAnswered ).reduce( ( a, b ) -> a + b ).getAsInt(),
                topic ) ).sorted( ( a, b ) -> Integer.compare( b.usersDone, a.usersDone ) ).map(
                surveyRating -> surveyRating.topic ).peek( topic -> topic.setSurveys(
                topic.getSurveys().stream().sorted( ( a, b ) -> b.getDate().compareTo( a.getDate() ) ).limit(
                        limit ).collect( Collectors.toList() ) ) ).collect( Collectors.toList() );
    }

    @Override
    public Optional<Topic> getTopicByName( String name ){
        return Optional.ofNullable( topicRepository.findOne( name ) );
    }

    @Override
    public void saveSurvey( Survey survey ){
        surveyRepository.save( survey );
    }

    @Override
    public void deleteSurvey( Survey survey ){
        surveyRepository.delete( survey.getId() );
    }

    class SurveyRating{
        Integer usersDone;
        Topic   topic;

        SurveyRating( Integer usersDone, Topic topic ){
            this.usersDone = usersDone;
            this.topic = topic;
        }
    }
}
