package ru.ssau.exceptions;

import java.nio.file.Path;

/***
 * when
 * @see com.fasterxml.jackson.databind.ObjectMapper can't map any object to JSON
 */
public class SerializationException extends RuntimeException{
    public SerializationException( Path path ){
        super( "Serialization exception " + path.toString() );
    }
}
