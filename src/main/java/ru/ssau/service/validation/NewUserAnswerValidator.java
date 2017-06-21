package ru.ssau.service.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.ssau.DAO.DAO;
import ru.ssau.DAO.enums.DeserializeSurveyOptions;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;
import ru.ssau.domain.UserAnswer;
import ru.ssau.service.SurveyService;
import ru.ssau.service.UserService;

import java.nio.file.Files;
import java.util.Optional;

@Component
public class NewUserAnswerValidator implements Validator{

    @Autowired
    private UserService   userService;
    @Autowired
    private SurveyService surveyService;
    @Autowired
    private DAO dao;


    @Override
    public boolean supports( Class<?> aClass ){
        return UserAnswer.class.equals( aClass );
    }

    @Override
    public void validate( Object o, Errors errors ){
        // TODO: 21.06.17
        UserAnswer userAnswer = ( UserAnswer ) o;
        try{
            userService.getUser( userAnswer.getUser().getLogin() ).orElseThrow( () -> new IllegalArgumentException( "Нет такого пользователя " + userAnswer.getUser().getLogin() ) );
            Survey survey = surveyService.getSurveyById( userAnswer.getSurvey().getId() , DeserializeSurveyOptions.QUESTIONS ).orElseThrow( () -> new IllegalArgumentException( "Нет такой анкеты " + userAnswer.getSurvey().getId() ) );
            if( survey.getQuestions().size() != userAnswer.getAnswers().size() )
                throw new IllegalArgumentException( "Несоответствие ответа анкете" );
            for( int i = 0 , end = survey.getQuestions().size() ; i < end ; i++ )
                if( survey.getQuestions().get( i ).getAnswers().size() <= userAnswer.getAnswers().get( i ) )
                    throw new IllegalArgumentException( "Несоответствие ответа анкете" );
        }catch( InterruptedException e ){
            e.printStackTrace();
        }
    }

//    0: все хорошо
//    1: не найден пользователь
//    2: не найдена анкета
//    3: не соответствие ответов анкете
//    4: анкета уже пройдена пользователем

    public Integer validate( UserAnswer userAnswer ) throws InterruptedException{
        Optional<User> user = userService.getUser( userAnswer.getUser().getLogin() );
        if( ! user.isPresent() )
            return 1;
        Optional<Survey> survey = surveyService.getSurveyById( userAnswer.getSurvey().getId() , DeserializeSurveyOptions.QUESTIONS );
        if( ! survey.isPresent() )
            return 2;
        if( dao.isUserAnswerAlreadyExists( userAnswer ) )
            return 4;
        if( survey.get().getQuestions().size() != userAnswer.getAnswers().size() )
            return 3;
        for( int i = 0 , end = survey.get().getQuestions().size() ; i < end ; i++ )
            if( survey.get().getQuestions().get( i ).getAnswers().size() <= userAnswer.getAnswers().get( i ) )
                return 3;
        return 0;
    }
}
