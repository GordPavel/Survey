package ru.ssau.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/***
 * If database couldn't find topic
 */

@ResponseStatus( HttpStatus.NOT_FOUND )
public class CategoryNotFoundException extends RuntimeException{
    public CategoryNotFoundException( String name ){
        super( "could not find category '" + name + "'." );
    }
}
