package ru.ssau.domain;

public class Answer{

    private Integer id;
    private String  name;

    public Answer(){
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
