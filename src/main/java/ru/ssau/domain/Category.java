package ru.ssau.domain;

import java.util.List;
import java.util.Objects;

public class Category{
    private String       name;
    private List<Survey> surveys;

    public String getName(){
        return name;
    }

    public Category(){
    }

    public void setName( String name ){
        this.name = name;
    }

    public List<Survey> getSurveys(){
        return surveys;
    }

    public void setSurveys( List<Survey> surveys ){
        this.surveys = surveys;
    }

    @Override
    public boolean equals( Object obj ){
        if( !( obj instanceof Category ) ) return false;
        Category category = ( Category ) obj;
        return Objects.equals( category.name, this.name ) && Objects.equals( category.surveys, this.surveys );
    }
}
