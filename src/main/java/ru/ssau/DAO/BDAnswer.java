package ru.ssau.DAO;

import ru.ssau.domain.Answer;

public class BDAnswer{
    Integer id;
    private String  name;

    public BDAnswer(){
    }

    BDAnswer( Answer answer ){
        this.id = answer.getId();
        this.name = answer.getName();
    }

    Answer toAnswer(){
        Answer answer = new Answer();
        answer.setId( this.id );
        answer.setName( this.name );
        answer.setUsersAnswered( 0 );
        return answer;
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
}
