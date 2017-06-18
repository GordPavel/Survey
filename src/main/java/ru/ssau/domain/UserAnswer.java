package ru.ssau.domain;

import java.util.List;

public class UserAnswer{
    private Survey survey;
    private User   user;
    private List<Integer>   answers;

    public UserAnswer(){
    }

    public Survey getSurvey(){
        return survey;
    }

    public void setSurvey( Survey survey ){
        this.survey = survey;
    }

    public User getUser(){
        return user;
    }

    public void setUser( User user ){
        this.user = user;
    }

    public List<Integer> getAnswers(){
        return answers;
    }

    public void setAnswers( List<Integer> answers ){
        this.answers = answers;
    }
}
