package ru.ssau.exceptions;

import java.nio.file.Path;

public class DeserializationException extends RuntimeException{
    public DeserializationException( Path path ){
        super( "Deserialization exception " + path.toString() );
    }
}
