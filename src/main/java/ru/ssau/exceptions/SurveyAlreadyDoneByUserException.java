package ru.ssau.exceptions;

public class SurveyAlreadyDoneByUserException extends RuntimeException{
    public SurveyAlreadyDoneByUserException( String login, Integer id ){
        super( "User " + login + " name  already done survey " + id );
    }
}
