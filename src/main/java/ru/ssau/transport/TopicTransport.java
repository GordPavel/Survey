package ru.ssau.transport;

import java.util.List;

public class TopicTransport{
    String                name;
    List<SurveyTransport> surveys;

    public TopicTransport( String name, List<SurveyTransport> surveys ){
        this.name = name;
        this.surveys = surveys;
    }

    public String getName(){
        return name;
    }

    public List<SurveyTransport> getSurveys(){
        return surveys;
    }
}
