package ru.ssau.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Survey{
    private Integer          id;
    private String           name;
    private String           comment;
    private List<UserAnswer> answers;
    private List<Question>   questions;
    private User             creator;
    private Date             date;
    private Category         category;

    public Date getDate(){
        return date;
    }

    public void setDate( Date date ){
        this.date = date;
    }

    public Survey(){
    }

    public Survey( Integer id ){
        this.id = id;
        name = "survey" + id;
        comment = "test";
        questions = Stream.iterate( new Question( 1 ), new UnaryOperator<Question>(){
            int i = 0;

            @Override
            public Question apply( Question question ){
                return new Question( ++i );
            }
        } ).limit( ( int ) ( 1 + ( long ) ( Math.random() * ( 11 - 1 ) ) ) ).collect( Collectors.toList() );
    }

    public Survey( Integer id, String name, String comment, User creator, Category category ){
        this.id = id;
        this.name = name;
        this.comment = comment;
        this.creator = creator;
        this.category = category;
        this.questions = Stream.iterate( new Question( 1 ), new UnaryOperator<Question>(){
            int i = 0;

            @Override
            public Question apply( Question question ){
                return new Question( ++i );
            }
        } ).limit( ( int ) ( 1 + ( long ) ( Math.random() * ( 11 - 1 ) ) ) ).collect( Collectors.toList() );
    }

    public Integer getId(){
        return id;
    }

    public List<Question> getQuestions(){
        return questions;
    }

    public void setQuestions( List<Question> questions ){
        this.questions = questions;
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

    public String getComment(){
        return comment;
    }

    public void setComment( String comment ){
        this.comment = comment;
    }

    public List<UserAnswer> getAnswers(){
        return answers;
    }

    @JsonIgnore
    public Integer getUsersDone(){
        if( answers == null ) return 0;
        return answers.size();
    }

    public void setAnswers( List<UserAnswer> answers ){
        this.answers = answers;
    }

    public User getCreator(){
        return creator;
    }

    public Category getCategory(){
        return category;
    }

    public void setCategory( Category category ){
        this.category = category;
    }

    public void setCreator( User creator ){
        this.creator = creator;
    }

    @Override
    public boolean equals( Object obj ){
        if( !( obj instanceof Survey ) ) return false;
        Survey survey = ( Survey ) obj;
        return Objects.equals( survey.name, this.name ) && Objects.equals( survey.comment, this.comment ) &&
               Objects.equals( survey.answers, this.answers ) && Objects.equals( survey.questions, this.questions ) &&
               Objects.equals( survey.creator, this.creator ) && Objects.equals( survey.category, this.category );
    }
}
