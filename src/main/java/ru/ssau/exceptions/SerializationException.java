package ru.ssau.exceptions;

import java.nio.file.Path;

public class SerializationException extends RuntimeException{
    public SerializationException( Path path ){
        super( "Serialization exception " + path.toString() );
    }
}
