package ru.ssau.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Question{

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private Integer      id;
    @Column( name = "name" )
    private String       name;
    @OneToMany
    private List<Answer> answers;
    @ManyToOne
    @JoinColumn( name = "survey_id", referencedColumnName = "id" )
    private Survey survey;

    public Question(){
    }

    public Integer getId(){
        return id;
    }

    public void setId( Integer id ){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName( String name ){
        this.name = name;
    }

    public List<Answer> getAnswers(){
        return answers;
    }

    public void setAnswers( List<Answer> answers ){
        this.answers = answers;
    }

    public Survey getSurvey(){
        return survey;
    }

    public void setSurvey( Survey survey ){
        this.survey = survey;
    }
}
