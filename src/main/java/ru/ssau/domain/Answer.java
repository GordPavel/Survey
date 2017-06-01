package ru.ssau.domain;

import javax.persistence.*;

@Entity
@Table( name = "answer" )
public class Answer{

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private Integer  id;
    @Column( name = "name" )
    private String   name;
    @ManyToOne
    @JoinColumn( name = "question_id", referencedColumnName = "id" )
    private Question question;

    public Answer(){
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

    public Question getQuestion(){
        return question;
    }

    public void setQuestion( Question question ){
        this.question = question;
    }
}
