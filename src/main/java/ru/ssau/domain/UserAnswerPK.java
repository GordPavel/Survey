package ru.ssau.domain;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class UserAnswerPK implements Serializable{

    @ManyToOne
    @JoinColumn( name = "user_login", referencedColumnName = "login" )
    private User user;

    @ManyToOne
    @JoinColumn( name = "survey_idsurvey", referencedColumnName = "id" )
    private Survey survey;

    public User getUser(){
        return user;
    }

    public void setUser( User user ){
        this.user = user;
    }

    public Survey getSurvey(){
        return survey;
    }

    public void setSurvey( Survey survey ){
        this.survey = survey;
    }
}
