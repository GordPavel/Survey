package ru.ssau.DAO.enums;

import java.util.Arrays;

public enum DeserializeSurveyOptions{
    USERS, QUESTIONS, CREATOR, CATEGORY;

    public static DeserializeSurveyOptions[] fromStrings( String... options ){
        return ( DeserializeSurveyOptions[] ) Arrays.stream( options ).map( s -> {
            switch( s ){
                case "users":
                    return USERS;
                case "questions":
                    return QUESTIONS;
                case "creator":
                    return CREATOR;
                case "category":
                    return CREATOR;
                default:
                    return null;
            }
        } ).toArray();
    }
}
