package ru.ssau.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( HttpStatus.NOT_FOUND )
public class UserAnswerNotFoundException extends RuntimeException{
    public UserAnswerNotFoundException( String userlogin , Integer surveyId ){
        super( "Couldn't find user answer " + userlogin + "_" + surveyId );
    }
}
