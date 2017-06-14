package ru.ssau.domain;

import java.util.List;

public class User{

    private String           login;
    private String           password;
    private String           name;
    private String           lastName;
    private UserRoles        role;
    private List<UserAnswer> answers;
    private List<Survey>     madeSurveys;

    public User(){
    }

    public User( String login ){
        this.login = login;
        role = UserRoles.USER;
    }

    public User( String login, String password ){
        this.login = login;
        this.password = password;
    }

    public User( String login, String password, String name, String lastName, UserRoles role ){
        this.login = login;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
        this.role = role;
    }

    public String getLogin(){
        return login;
    }

    public void setLogin( String login ){
        this.login = login;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword( String password ){
        this.password = password;
    }

    public String getName(){
        return name;
    }

    public void setName( String name ){
        this.name = name;
    }

    public String getLastName(){
        return lastName;
    }

    public void setLastName( String lastName ){
        this.lastName = lastName;
    }

    public UserRoles getRole(){
        return role;
    }

    public void setRole( UserRoles role ){
        this.role = role;
    }

    public List<UserAnswer> getAnswers(){
        return answers;
    }

    public void setAnswers( List<UserAnswer> answers ){
        this.answers = answers;
    }

    public List<Survey> getMadeSurveys(){
        return madeSurveys;
    }

    public void setMadeSurveys( List<Survey> madeSurveys ){
        this.madeSurveys = madeSurveys;
    }
}
