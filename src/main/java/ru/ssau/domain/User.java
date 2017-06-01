package ru.ssau.domain;

import java.util.List;

public class User{

    private String           login;
    private String           password;
    private String           name;
    private String           lastName;
    private UserRoles        role;
    private List<UserAnswer> answers;
    private List<Survey>     created;

    public User(){
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

    public List<Survey> getCreated(){
        return created;
    }

    public void setCreated( List<Survey> created ){
        this.created = created;
    }
}
