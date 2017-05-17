package ru.ssau.domain;

import java.io.File;

public class Answer{

    private Integer id;
    private String  name;
    private Integer answered;
    private File    file;

    public Answer(){
    }

    public Answer( Integer id ){
        this.id = id;
        name = "answer" + id;
        answered = ( int ) ( 1 + ( long ) ( Math.random() * ( 6 - 1 ) ) );
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

    public Integer getAnswered(){
        return answered;
    }

    public void setAnswered( Integer answered ){
        this.answered = answered;
    }

    public File getFile(){
        return file;
    }

    public void setFile( File file ){
        this.file = file;
    }
}
