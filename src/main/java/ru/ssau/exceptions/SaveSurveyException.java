package ru.ssau.exceptions;

public class SaveSurveyException extends RuntimeException{
    public SaveSurveyException( String string  ){
        super( "couldn't save Survey without " + string );
    }
}
