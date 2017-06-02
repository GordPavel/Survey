package ru.ssau.domain;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class UserAnswerPK implements Serializable{

    @ManyToOne
    private User user;

    @ManyToOne
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
