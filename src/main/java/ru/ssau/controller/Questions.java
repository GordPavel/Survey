package ru.ssau.controller;

import ru.ssau.domain.Answer;
import ru.ssau.domain.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Questions{
    private Integer      id;
    private String       name;
    private List<Answer> answers;
    private Integer      other;
    private Answer       userAnsweredOn;

    public Questions(){
    }

    Questions( Question question ){
        this.id = question.getId();
        this.name = question.getName();
        answers = new ArrayList<>();
        List<Answer> allAnswers = question.getAnswers().stream()
                .sorted( ( o1, o2 ) -> Integer.compare( o2.getUsersAnswered() , o1.getUsersAnswered() ) )
                .collect( Collectors.toList() );
        if( allAnswers.size() < 5 ){
            answers.addAll( allAnswers.subList( 0, allAnswers.size() ) );
            this.other = null;
        }else{
            answers.addAll( allAnswers.subList( 0, 4 ) );
            this.other = allAnswers.subList( 4, allAnswers.size() ).stream().mapToInt(
                    Answer::getUsersAnswered ).sum();
        }
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

    public List<Answer> getAnswers(){
        return answers;
    }

    public void setAnswers( List<Answer> answers ){
        this.answers = answers;
    }

    public Integer getOther(){
        return other;
    }

    public void setOther( Integer other ){
        this.other = other;
    }

    public Answer getUserAnsweredOn(){
        return userAnsweredOn;
    }

    public void setUserAnsweredOn( Answer userAnsweredOn ){
        this.userAnsweredOn = userAnsweredOn;
    }
}
