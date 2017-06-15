package ru.ssau.DAO.survey;

import java.util.Arrays;

public enum DeserializeSurveyOptions{
    ANSWERS, QUESTIONS, CREATOR, CATEGORY;

    public static DeserializeSurveyOptions[] fromStrings( String... options ){
        return ( DeserializeSurveyOptions[] ) Arrays.stream( options ).map( s -> {
            switch( s ){
                case "users":
                    return ANSWERS;
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
