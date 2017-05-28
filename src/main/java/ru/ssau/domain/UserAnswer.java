package ru.ssau.domain;

class UserAnswer{
    private Survey survey;
    private User   user;
    private String answers;

    public UserAnswer(){
    }

    public Survey getSurvey(){
        return survey;
    }

    public void setSurvey( Survey survey ){
        this.survey = survey;
    }

    public User getUser(){
        return user;
    }

    public void setUser( User user ){
        this.user = user;
    }

    public String getAnswers(){
        return answers;
    }

    public void setAnswers( String answers ){
        this.answers = answers;
    }
}
