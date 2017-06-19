package ru.ssau.DAO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.ssau.domain.*;

import java.util.Date;
import java.util.List;

public class BDSurvey{

    public BDSurvey(){
    }

    BDSurvey( Survey survey ){
        this.id = survey.getId();
        this.name = survey.getName();
        this.comment = survey.getComment();
        this.creator = survey.getCreator().getLogin();
        this.date = survey.getDate();
        this.categoryName = survey.getCategory().getName();
    }

    @JsonIgnore
    public Survey toSurvey(){
        Survey survey = new Survey();
        survey.setId( this.id );
        survey.setName( this.name );
        survey.setComment( this.comment );
        survey.setAnswers( this.answers );
        survey.setQuestions( this.questions );
        survey.setCreator( this.userCreator );
        survey.setDate( this.date );
        survey.setCategory( this.category );
        return survey;
    }

    private Integer          id;
    private String           name;
    private String           comment;
    private String           creator;
    private Date             date;
    private String           categoryName;

    @JsonIgnore
    private User             userCreator;
    @JsonIgnore
    private List<Question>   questions;
    @JsonIgnore
    private List<UserAnswer> answers;
    @JsonIgnore
    private Category         category;

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

    public String getCategoryName(){
        return categoryName;
    }

    void setCategoryName( String categoryName ){
        this.categoryName = categoryName;
    }

    User getUserCreator(){
        return userCreator;
    }

    void setUserCreator( User userCreator ){
        this.userCreator = userCreator;
    }

    List<Question> getQuestions(){
        return questions;
    }

    void setQuestions( List<Question> questions ){
        this.questions = questions;
    }

    List<UserAnswer> getAnswers(){
        return answers;
    }

    void setAnswers( List<UserAnswer> answers ){
        this.answers = answers;
    }

    Category getCategory(){
        return category;
    }

    void setCategory( Category category ){
        this.category = category;
    }
}
