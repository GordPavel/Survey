package ru.ssau.domain;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
public class Survey{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    @Column( name = "id" )
    private Integer id;
    @Basic
    @Column( name = "comment" )
    private String  comment;

    @Basic
    @Column( name = "name", nullable = false, length = 45 )
    private String name;
    @Temporal( TemporalType.DATE )
    @Column( name = "madeTime" )
    private Date   date;

    @ManyToOne
    @JoinColumn( name = "topic_name", referencedColumnName = "name" )
    private Topic topic;

    @OneToMany( fetch = FetchType.LAZY , mappedBy = "pk.survey" )
    private List<UserAnswer> answers;

    @ManyToOne
    @JoinColumn( name = "userMade", referencedColumnName = "login" )
    private User creator;

    @OneToMany( fetch = FetchType.EAGER, mappedBy = "survey" )
    private List<Question> questions;

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

    public void setDate( Timestamp madeTime ){
        this.date = madeTime;
    }

    public Topic getTopic(){
        return topic;
    }

    public void setTopic( Topic topic ){
        this.topic = topic;
    }

    public Integer getUsersDone(){
        return getAnswers().size();
    }

    public List<UserAnswer> getAnswers(){
        return answers;
    }

    public void setAnswers( List<UserAnswer> answers ){
        this.answers = answers;
    }

    public User getCreator(){
        return creator;
    }

    public void setCreator( User creator ){
        this.creator = creator;
    }

    public void setDate( Date date ){
        this.date = date;
    }

    public List<Question> getQuestions(){
        return questions;
    }

    public void setQuestions( List<Question> questions ){
        this.questions = questions;
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
