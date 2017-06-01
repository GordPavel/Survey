package ru.ssau.domain;

import java.util.List;

public class Topic{
    private String       name;
    private List<Survey> surveys;

    public String getName(){
        return name;
    }

    public Topic( String name, List<Survey> surveys ){
        this.name = name;
        this.surveys = surveys;
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
}
