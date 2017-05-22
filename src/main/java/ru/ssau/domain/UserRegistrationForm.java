package ru.ssau.domain;

import org.springframework.web.multipart.MultipartFile;

public class UserRegistrationForm{
    private String        login;
    private String        password;
    private String        passwordRepeat;
    private String        name;
    private String        lastName;
    private MultipartFile file;

    public UserRegistrationForm(){
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

    public String getPasswordRepeat(){
        return passwordRepeat;
    }

    public void setPasswordRepeat( String passwordRepeat ){
        this.passwordRepeat = passwordRepeat;
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

    public MultipartFile getFile(){
        return file;
    }

    public void setFile( MultipartFile file ){
        this.file = file;
    }
}
