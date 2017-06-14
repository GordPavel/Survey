package ru.ssau.domain;

import java.util.Date;
import java.util.List;
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

    public Integer getUsersDone(){
        if( answers == null ) throw new IllegalArgumentException( "Ответы не загружены" );
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
}
