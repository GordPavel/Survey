package ru.ssau.DAO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;
import ru.ssau.domain.UserAnswer;
import ru.ssau.domain.UserRoles;

import java.util.List;

public class BDUser{

    private String           login;
    private String           password;
    private String           name;
    private String           lastName;
    private String           role;

    @JsonIgnore
    private List<UserAnswer> answers;
    @JsonIgnore
    private List<Survey>     madeByUserSurveys;

    public BDUser(){
    }

    BDUser( User user ){
        login = user.getLogin();
        password = user.getPassword();
        name = user.getName();
        lastName = user.getLastName();
        role = user.getRole().name();
    }

    User toUser(){
        User user = new User();
        user.setLogin( this.login );
        user.setPassword( this.password );
        user.setName( this.name );
        user.setLastName( this.lastName );
        user.setRole( UserRoles.valueOf( this.role ) );
        user.setAnswers( this.answers );
        user.setMadeSurveys( this.madeByUserSurveys );
        return user;
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

    public String getRole(){
        return role;
    }

    public void setRole( String role ){
        this.role = role;
    }

    public List<UserAnswer> getAnswers(){
        return answers;
    }

    void setAnswers( List<UserAnswer> answers ){
        this.answers = answers;
    }

    public List<Survey> getMadeByUserSurveys(){
        return madeByUserSurveys;
    }

    void setMadeByUserSurveys( List<Survey> madeByUserSurveys ){
        this.madeByUserSurveys = madeByUserSurveys;
    }
}
