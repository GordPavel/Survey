package ru.ssau.domain;

import java.io.File;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Survey{

    private Integer        id;
    private String         name;
    private String         comment;
    private List<User>     users;
    private List<Question> questions;
    private User           madeByUser;
    private Date           date;

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
        users = Stream.iterate( new User( "login" + 1 ), new UnaryOperator<User>(){
            int i = 1;
            @Override
            public User apply( User t ){
                return new User( "login" + ( ++i ) );
            }
        } ).limit( ( int )( 1 + ( long ) ( Math.random() * ( 6 - 1 ) ) ) ).collect( Collectors.toList() );
        questions = Stream.iterate( new Question( 1 ), new UnaryOperator<Question>(){
            int i = 1;
            @Override
            public Question apply( Question t ){
                return new Question( ++i );
            }
        } ).limit( ( int )( 1 + ( long ) ( Math.random() * ( 6 - 1 ) ) ) ).collect( Collectors.toList() );
        madeByUser = users.get( 0 );
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

    public void setUsers( List<User> users ){
        this.users = users;
    }

    public Integer getUsersDone(){
        return users.size();
    }

    public User getMadeByUser(){
        return madeByUser;
    }

    public void setMadeByUser( User madeByUser ){
        this.madeByUser = madeByUser;
    }
}
