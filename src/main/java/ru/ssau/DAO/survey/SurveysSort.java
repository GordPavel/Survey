package ru.ssau.DAO.survey;

public enum SurveysSort{

    TIME, ANSWERS;

    SurveysSort toSortType( String type ){
        switch( type ){
            case "time":
                return TIME;
            case "users":
                return ANSWERS;
            default:
                throw new IllegalArgumentException( "Нет такого типа" );
        }
    }
}
