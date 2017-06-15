package ru.ssau.DAO.user;

import org.springframework.beans.factory.annotation.Autowired;
import ru.ssau.DAO.survey.DeserializeSurveyOptions;
import ru.ssau.DAO.survey.SurveyDAO;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;
import ru.ssau.domain.UserAnswer;
import ru.ssau.domain.UserRoles;

import java.io.IOException;
import java.util.List;

public class BDUser{

    private String           login;
    private String           password;
    private String           name;
    private String           lastName;
    private String           role;
    private List<UserAnswer> answers;
    private List<Survey>     madeByUserSurveys;
    @Autowired
    private SurveyDAO        surveyDAO;

    public BDUser( User user ){
        login = user.getLogin();
        password = user.getPassword();
        name = user.getName();
        lastName = user.getLastName();
        role = user.getRole().name();
    }

    public User toUser(){
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

    public BDUser addAnswers(){
        try{
            this.answers = getUserAnswers();
        }catch( IOException e ){
            throw new IllegalArgumentException( "Не удалось загрузить ответы пользователя " + login );
        }
        return this;
    }

    private List<UserAnswer> getUserAnswers() throws IOException{
        // TODO: 14.06.17
        return null;
    }

    public BDUser addMadeByUserSurveys( DeserializeSurveyOptions... options ){
        try{
            this.madeByUserSurveys = getMadeByUserSurveys( options );
        }catch( IOException e ){
            throw new IllegalArgumentException( "Не удалось загрузить анкеты, сделанные пользователем " + login );
        }
        return this;
    }

    private List<Survey> getMadeByUserSurveys( DeserializeSurveyOptions... options ) throws IOException{
        // TODO: 15.06.17
        return null;
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
}
