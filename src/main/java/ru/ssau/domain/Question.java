package ru.ssau.domain;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Question{

    private Integer      id;
    private String       name;
    private List<Answer> answers;

    public Question(){
    }

    public Question( Integer id ){
        this.id = id;
        name = "question" + id;
        answers = Stream.iterate( new Answer( 1 ), new UnaryOperator<Answer>(){
            int i = 1;
            @Override
            public Answer apply( Answer t ){
                return new Answer( ++i );
            }
        } ).limit( ( int ) ( 1 + ( long ) ( Math.random() * ( 6 - 1 ) ) ) ).collect( Collectors.toList() );
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
}
