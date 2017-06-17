package ru.ssau.domain;

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
}
