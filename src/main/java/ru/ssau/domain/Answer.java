package ru.ssau.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public class Answer{
    private Integer id;
    private String  name;
    private Integer usersAnswered;

    public Answer(){
    }

    public Answer( Integer id ){
        this.id = id;
        name = "answer" + id;
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

    public Integer getUsersAnswered(){
        return usersAnswered;
    }

    public void setUsersAnswered( Integer usersAnswered ){
        this.usersAnswered = usersAnswered;
    }

    @JsonIgnore
    public void incrementUsersAnswered(){
        usersAnswered++;
    }

    @Override
    public boolean equals( Object obj ){
        if( !( obj instanceof Answer ) ) return false;
        Answer answer = ( Answer ) obj;
        if( ! Objects.equals( answer.name, this.name ) )
            return false;
        if( this.usersAnswered != null && answer.usersAnswered != null && Objects.equals( answer.usersAnswered, this.usersAnswered ) )
            return false;
        return true;
    }
}
