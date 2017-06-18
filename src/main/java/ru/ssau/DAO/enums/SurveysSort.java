package ru.ssau.DAO.enums;

public enum SurveysSort{

    TIME, USERS;

    SurveysSort toSortType( String type ){
        switch( type ){
            case "time":
                return TIME;
            case "users":
                return USERS;
            default:
                throw new IllegalArgumentException( "Нет такого типа" );
        }
    }
}
