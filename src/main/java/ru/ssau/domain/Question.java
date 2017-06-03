package ru.ssau.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Question{
    @Id
    @Column( name = "id", nullable = false )
    @GeneratedValue( strategy = GenerationType.AUTO )
    private Integer id;

    @Basic
    @Column( name = "name", nullable = false )
    private String name;

    @ManyToOne
    @JoinColumn( name = "survey_id", referencedColumnName = "id" )
    private Survey survey;

    @OneToMany( fetch = FetchType.EAGER, mappedBy = "question" )
    private List<Answer> answers;

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

    public Survey getSurvey(){
        return survey;
    }

    public void setSurvey( Survey survey ){
        this.survey = survey;
    }

    public List<Answer> getAnswers(){
        return answers;
    }

    public void setAnswers( List<Answer> answers ){
        this.answers = answers;
    }

    @Override
    public boolean equals( Object o ){
        if( this == o )
            return true;
        if( o == null || getClass() != o.getClass() )
            return false;

        Question question = ( Question ) o;

        if( id != null ? !id.equals( question.id ) : question.id != null )
            return false;
        if( name != null ? !name.equals( question.name ) : question.name != null )
            return false;

        return true;
    }

    @Override
    public int hashCode(){
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        return result;
    }
}
