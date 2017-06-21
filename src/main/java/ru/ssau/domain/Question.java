package ru.ssau.domain;

import java.util.List;
import java.util.Objects;
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

    @Override
    public boolean equals( Object obj ){
        if( !( obj instanceof Question ) ) return false;
        Question question = ( Question ) obj;
        return Objects.equals( question.id, this.id ) && Objects.equals( question.name, this.name ) &&
               Objects.equals( question.answers, this.answers );
    }
}
