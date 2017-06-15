package ru.ssau.DAO.survey;

import org.springframework.beans.factory.annotation.Autowired;
import ru.ssau.DAO.user.DeserializeUserOptions;
import ru.ssau.DAO.user.UserDAO;
import ru.ssau.domain.*;
import ru.ssau.exceptions.UserNotFoundException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class BDSurvey{

    @Autowired
    private UserDAO userDAO;

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

    Survey toSurvey(){
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

    public BDSurvey addCreator( DeserializeUserOptions... options ){
        try{
            this.userCreator = getCreator( this.creator , options );
        }catch( IOException e ){
            throw new IllegalArgumentException( "Не удалось загразить пользователя " + this.creator );
        }
        return this;
    }

    private User getCreator( String login , DeserializeUserOptions... options ) throws IOException{
        return userDAO.listUsersByPredicate( path -> path.toString().substring( userDAO.getDirectoryNameLength() ).equals( login ) ,
                                        1 ,
                                             options )
                .stream()
                .findFirst()
                .orElseThrow( () -> new UserNotFoundException( login ) );
    }

    public BDSurvey addQuestions(){
        this.questions = getQuestions( this.id );
        return this;
    }

    private List<Question> getQuestions( Integer id ){
        // TODO: 13.06.17 Найти впоросы
        return null;
    }

    public BDSurvey addAnswers(){
        this.answers = getAnswers( this.id );
        return this;
    }

    private List<UserAnswer> getAnswers( Integer id ){
        // TODO: 13.06.17 Найти ответы
        return null;
    }

    public BDSurvey addCategory(){
        this.category = getCategory( this.categoryName );
        return this;
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
    private String  categoryName;
    private User    userCreator;
    private List<Question> questions;
    private List<UserAnswer> answers;
    private Category category;


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
        return categoryName;
    }

    public void setCategory( String category ){
        this.categoryName = category;
    }
}
