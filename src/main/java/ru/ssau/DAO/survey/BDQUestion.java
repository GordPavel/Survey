package ru.ssau.DAO.survey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.ssau.domain.Answer;
import ru.ssau.domain.Question;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

public class BDQuestion{
    private Integer id;
    private String  name;

    public BDQuestion(){
    }

    @JsonIgnore
    private List<Answer> answers;

    public BDQuestion( Question question ){
        this.id = question.getId();
        this.name = question.getName();
    }

    public Question toQuestion(){
        Question question = new Question();
        question.setId( this.id );
        question.setName( this.name );
        question.setAnswers( this.answers );
        return question;
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
