package ru.ssau.domain;

import javax.persistence.*;

@Entity
@Table( name = "user_has_survey", schema = "survey" )
@AssociationOverrides( { @AssociationOverride( name = "pk.user", joinColumns = @JoinColumn( name = "user_login" ) ) ,
                         @AssociationOverride( name = "pk.survey", joinColumns = @JoinColumn( name = "survey_idsurvey" ) ) } )
public class UserAnswer{

    @EmbeddedId
    private UserAnswerPK pk = new UserAnswerPK();
    @Basic
    @Column( name = "userAnswer" )
    private String userAnswer;

    public UserAnswerPK getPk(){
        return pk;
    }

    public void setPk( UserAnswerPK pk ){
        this.pk = pk;
    }

    public String getUserAnswer(){
        return userAnswer;
    }

    public void setUserAnswer( String userAnswer ){
        this.userAnswer = userAnswer;
    }

    @Transient
    public User getUser(){
        return getPk().getUser();
    }

    public void setUser( User user ){
        this.getPk().setUser( user );
    }

    @Transient
    public Survey getSurvey(){
        return getPk().getSurvey();
    }

    public void setSurvey( Survey survey ){
        this.getPk().setSurvey( survey );
    }
}
