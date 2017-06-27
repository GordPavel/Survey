package ru.ssau.controller;

import ru.ssau.domain.Category;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;
import ru.ssau.domain.UserAnswer;
import ru.ssau.exceptions.UserAnswerNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/***
 * Jusr container to show survey statistics
 */
public class SurveyStatistics{
    private Integer         id;
    private String          name;
    private String          comment;
    private List<Questions> questions;
    private User            creator;
    private Category        category;

    public SurveyStatistics(){
    }

    public SurveyStatistics( Survey survey ){
        this.id = survey.getId();
        this.name = survey.getName();
        this.comment = survey.getComment();
        this.creator = survey.getCreator();
        this.category = survey.getCategory();
        this.questions = survey.getQuestions().stream().map( Questions::new ).collect( Collectors.toList() );
    }

    public SurveyStatistics( Survey survey, String login ){
        this( survey );
        List<Integer> userAnswers = survey.getAnswers().stream()
                .filter( userAnswer -> userAnswer.getUser().getLogin().equals( login ) )
                .findAny().orElseThrow( () -> new UserAnswerNotFoundException( login , survey.getId() ) )
                .getAnswers();
        for( int i = 0, end = userAnswers.size() ; i < end ; i++ )
            this.questions.get( i ).setUserAnsweredOn( survey.getQuestions().get( i ).getAnswers().get( userAnswers.get( i ) ) );
    }

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

    public List<Questions> getQuestions(){
        return questions;
    }

    public void setQuestions( List<Questions> questions ){
        this.questions = questions;
    }

    public User getCreator(){
        return creator;
    }

    public void setCreator( User creator ){
        this.creator = creator;
    }

    public Category getCategory(){
        return category;
    }

    public void setCategory( Category category ){
        this.category = category;
    }
}
