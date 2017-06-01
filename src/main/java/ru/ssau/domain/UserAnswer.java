package ru.ssau.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

class UserAnswer{
    private Survey survey;
    private User   user;
    private String answers;

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

    public List<Integer> getAnswers() throws IOException{
        return new ObjectMapper().readValue( this.answers, new TypeReference<List<Integer>>(){
        } );
    }

    public void setAnswers( List<Integer> answers ) throws JsonProcessingException{
        // TODO: 01.06.17 Проверки
        this.answers = new ObjectMapper().writeValueAsString( answers );
    }
}
