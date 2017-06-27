package ru.ssau.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/***
 * If database couldn't find survey
 */

@ResponseStatus( HttpStatus.NOT_FOUND )
public class SurveyNotFoundException extends RuntimeException{

    public SurveyNotFoundException( Integer id ){
        super( "could not find survey '" + id + "'." );
    }
}
