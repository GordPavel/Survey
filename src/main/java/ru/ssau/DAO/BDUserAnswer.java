package ru.ssau.DAO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;
import ru.ssau.domain.UserAnswer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BDUserAnswer{
    private String  userLogin;
    private Integer surveyId;
    private String  answers;

    @JsonIgnore
    private User user;
    @JsonIgnore
    private Survey survey;
    @JsonIgnore
    private List<Integer> answersList;

    public BDUserAnswer(){
    }

    public UserAnswer toUserAnswer(){
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setUser( this.user );
        userAnswer.setSurvey( this.survey );
        userAnswer.setAnswers( this.answersList );
        return userAnswer;
    }

    public String getUserLogin(){
        return userLogin;
    }

    public void setUserLogin( String userLogin ){
        this.userLogin = userLogin;
    }

    public Integer getSurveyId(){
        return surveyId;
    }

    public void setSurveyId( Integer surveyId ){
        this.surveyId = surveyId;
    }

    public String getAnswers(){
        return answers;
    }

    public void setAnswers( String answers ){
        this.answers = answers;
    }

    User getUser(){
        return user;
    }

    void setUser( User user ){
        this.user = user;
    }

    Survey getSurvey(){
        return survey;
    }

    void setSurvey( Survey survey ){
        this.survey = survey;
    }

    List<Integer> getAnswersList(){
        return answersList;
    }

    void setAnswersList( List<Integer> answersList ){
        this.answersList = answersList;
    }
}
