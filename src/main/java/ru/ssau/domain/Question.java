package ru.ssau.domain;

import java.util.List;

public class Question{

    private Integer      id;
    private String       name;
    private List<Answer> answers;

    public Question(){
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

    public List<Answer> getAnswers(){
        return answers;
    }

    public void setAnswers( List<Answer> answers ){
        this.answers = answers;
    }
}
