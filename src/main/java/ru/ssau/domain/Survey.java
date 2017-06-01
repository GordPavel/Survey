package ru.ssau.domain;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

public class Survey{

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private Integer id;
    @Column( name = "name" )
    private String  name;
    @Column( name = "comment" )
    private String  comment;

    private List<User>     users;
    @OneToMany
    private List<Question> questions;

    private User madeByUser;

    @Temporal( TemporalType.DATE )
    @Column( name = "madeTime" )
    private Date date;


    public Survey(){
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

    public String getComment(){
        return comment;
    }

    public void setComment( String comment ){
        this.comment = comment;
    }

    public Integer getUsersDone(){
        return getUsers().size();
    }

    public List<User> getUsers(){
        return users;
    }

    public void setUsers( List<User> users ){
        this.users = users;
    }

    public List<Question> getQuestions(){
        return questions;
    }

    public void setQuestions( List<Question> questions ){
        this.questions = questions;
    }

    public User getMadeByUser(){
        return madeByUser;
    }

    public void setMadeByUser( User madeByUser ){
        this.madeByUser = madeByUser;
    }

    public Date getDate(){
        return date;
    }

    public void setDate( Date date ){
        this.date = date;
    }
}
