package ru.ssau.DAO.survey;

import ru.ssau.domain.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

class DBSurvey{

    public DBSurvey(){
    }

    DBSurvey( Survey survey ){
        this.id = survey.getId();
        this.name = survey.getName();
        this.comment = survey.getComment();
        this.creator = survey.getCreator().getLogin();
        this.date = survey.getDate();
        this.category = survey.getCategory().getName();
    }

    Survey toSurvey( DeserializeSurveyOption... options ){
        List<DeserializeSurveyOption> deserializeSurveyOptions = Arrays.asList( options );
        Survey                        survey                   = new Survey();
        survey.setId( this.id );
        survey.setName( this.name );
        survey.setComment( this.comment );
        if( deserializeSurveyOptions.contains( DeserializeSurveyOption.CREATOR ) )
            survey.setCreator( getCreator( this.creator ) );
        if( deserializeSurveyOptions.contains( DeserializeSurveyOption.USERS ) )
            survey.setAnswers( getAnswers( this.id ) );
        if( deserializeSurveyOptions.contains( DeserializeSurveyOption.QUESTIONS ) )
            survey.setQuestions( getQuestions( this.id ) );
        if( deserializeSurveyOptions.contains( DeserializeSurveyOption.CATEGORY ) )
            survey.setCategory( getCategory( this.category ) );
        survey.setDate( this.date );
        return survey;
    }

    private User getCreator( String login ){
        // TODO: 13.06.17 Найти создателя
        return null;
    }

    private List<Question> getQuestions( Integer id ){
        // TODO: 13.06.17 Найти впоросы
        return null;
    }

    private List<UserAnswer> getAnswers( Integer id ){
        // TODO: 13.06.17 Найти ответы
        return null;
    }

    private Category getCategory( String name ){
        // TODO: 13.06.17 Найти категорию
        return null;
    }

    private Integer id;
    private String  name;
    private String  comment;
    private String  creator;
    private Date    date;
    private String  category;

    public Integer getId(){
        return id;
    }

    public void setId( Integer id ){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName( String name ){
        this.name = name;
    }

    public String getComment(){
        return comment;
    }

    public void setComment( String comment ){
        this.comment = comment;
    }

    public String getCreator(){
        return creator;
    }

    public void setCreator( String creator ){
        this.creator = creator;
    }

    public Date getDate(){
        return date;
    }

    public void setDate( Date date ){
        this.date = date;
    }

    public String getCategory(){
        return category;
    }

    public void setCategory( String category ){
        this.category = category;
    }
}
