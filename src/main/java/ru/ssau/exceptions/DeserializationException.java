package ru.ssau.exceptions;

import java.nio.file.Path;

/***
 * when
 * @see com.fasterxml.jackson.databind.ObjectMapper can't map JSON object to any object
 */
public class DeserializationException extends RuntimeException{
    public DeserializationException( Path path ){
        super( "Deserialization exception " + path.toString() );
    }
}
