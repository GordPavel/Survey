package ru.ssau.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/***
 * If database couldn't find user
 */

@ResponseStatus( HttpStatus.NOT_FOUND )
public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException( String login ){
        super( "could not find user '" + login + "'." );
    }
}
