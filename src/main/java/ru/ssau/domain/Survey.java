package ru.ssau.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Entity
public class Survey{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id", nullable = false, unique = true )
    private Integer id;
    @Basic
    @Column( name = "comment" )
    private String  comment;

    @Basic
    @Column( name = "name", nullable = false, length = 45 )
    private String name;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "madeTime" )
    private Date date;

    @ManyToOne
    @JoinColumn( name = "topic_name", referencedColumnName = "name" )
    private Topic topic;

    @ManyToOne
    @JoinColumn( name = "userMade", referencedColumnName = "login" )
    private User creator;

    @OneToMany( fetch = FetchType.LAZY, mappedBy = "survey", cascade = CascadeType.ALL )
    private List<Question> questions;

    @Transient
    private Integer usersAnswered;

    public Integer getId(){
        return id;
    }

    public void setId( Integer id ){
        this.id = id;
    }

    public String getComment(){
        return comment;
    }

    public void setComment( String comment ){
        this.comment = comment;
    }

    public String getName(){
        return name;
    }

    public void setName( String name ){
        this.name = name;
    }

    public Date getDate(){
        return date;
    }

    public void setDate( Date date ){
        this.date = date;
    }

    public Topic getTopic(){
        return topic;
    }

    public void setTopic( Topic topic ){
        this.topic = topic;
    }

    public Integer getUsersAnswered(){
        return usersAnswered;
    }

    public void setUsersAnswered( List<UserAnswer> list ){
        this.usersAnswered = list.size();
    }

    public User getCreator(){
        return creator;
    }

    public void setCreator( User creator ){
        this.creator = creator;
    }

    public List<Question> getQuestions(){
        return questions;
    }

    public void setQuestions( List<Question> questions ){
        this.questions = questions;
    }

    public void setAnswers( List<UserAnswer> list ){
        this.usersAnswered = list.size();
        list.stream().map( ( Function<UserAnswer, List<Integer>> ) answer -> {
            try{
                return new ObjectMapper().readValue( answer.getUserAnswer(), new TypeReference<List<Integer>>(){
                } );
            }catch( IOException e ){
                throw new IllegalArgumentException( "Ошибка деериализации ответов на анкету" );
            }
        } ).forEach( integers -> {
            for( int i = 0, end = this.getQuestions().size() ; i < end ; i++ ){
                this.getQuestions().get( i ).getAnswers().get( integers.get( i ) - 1 ).incrementUsersAnswered();
            }
        } );
    }

    @Override
    public boolean equals( Object o ){
        if( this == o )
            return true;
        if( o == null || getClass() != o.getClass() )
            return false;

        Survey survey = ( Survey ) o;

        if( id != null ? !id.equals( survey.id ) : survey.id != null )
            return false;
        if( comment != null ? !comment.equals( survey.comment ) : survey.comment != null )
            return false;
        if( name != null ? !name.equals( survey.name ) : survey.name != null )
            return false;
        if( date != null ? !date.equals( survey.date ) : survey.date != null )
            return false;

        return true;
    }

    @Override
    public int hashCode(){
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + ( comment != null ? comment.hashCode() : 0 );
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        result = 31 * result + ( date != null ? date.hashCode() : 0 );
        return result;
    }
}
