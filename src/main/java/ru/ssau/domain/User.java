package ru.ssau.domain;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class User{

    private String        login;
    private String        password;
    private String        name;
    private String        lastName;
    private UserRoles     role;
    private List<Survey>  doneSurveys;
    private List<Survey>  madeSurveys;
    private MultipartFile file;

    public User(){
    }

    public User( String login ){
        this.login = login;
        role = UserRoles.USER;
    }

    public User( String login, String password, MultipartFile file ){
        this.login = login;
        this.password = password;
        this.file = file;
    }

    public MultipartFile getFile(){
        return file;
    }

    public void setFile( MultipartFile file ){
        this.file = file;
    }

    public List<Survey> getDoneSurveys(){
        return doneSurveys;
    }

    public void setDoneSurveys( List<Survey> doneSurveys ){
        this.doneSurveys = doneSurveys;
    }

    public List<Survey> getMadeSurveys(){
        return madeSurveys;
    }

    public void setMadeSurveys( List<Survey> madeSurveys ){
        this.madeSurveys = madeSurveys;
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
}
