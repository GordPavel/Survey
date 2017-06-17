package ru.ssau.DAO.survey;

import ru.ssau.domain.Answer;

public class BDAnswer{
    Integer id;
    String  name;

    public BDAnswer(){
    }

    public BDAnswer( Answer answer ){
        this.id = answer.getId();
        this.name = answer.getName();
    }

    public Answer toAnswer(){
        Answer answer = new Answer();
        answer.setId( this.id );
        answer.setName( this.name );
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
