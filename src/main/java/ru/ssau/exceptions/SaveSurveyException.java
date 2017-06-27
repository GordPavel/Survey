package ru.ssau.exceptions;

/***
 * saving survey errors
 * @see ru.ssau.DAO.DatabaseUtils
 */
public class SaveSurveyException extends RuntimeException{
    public SaveSurveyException( String string  ){
        super( "couldn't save Survey without " + string );
    }
}
