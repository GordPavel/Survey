package ru.ssau.domain;

import javax.persistence.*;

@Entity
public class Answer{

    @Id
    @Column( name = "id", nullable = false, unique = true )
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Integer id;

    @Basic
    @Column( name = "name", nullable = false, length = 45 )
    private String name;

    @ManyToOne
    @JoinColumn( name = "question_id", referencedColumnName = "id" )
    private Question question;

    @Transient
    private Integer usersAnswered = 0;

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

    public Integer getUsersAnswered(){
        return usersAnswered;
    }

    public void incrementUsersAnswered(){
        this.usersAnswered++;
    }

    @Override
    public boolean equals( Object o ){
        if( this == o )
            return true;
        if( o == null || getClass() != o.getClass() )
            return false;

        Answer answer = ( Answer ) o;

        if( id != null ? !id.equals( answer.id ) : answer.id != null )
            return false;
        if( name != null ? !name.equals( answer.name ) : answer.name != null )
            return false;

        return true;
    }

    @Override
    public int hashCode(){
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        return result;
    }

    @Override
    public String toString(){
        return super.toString() + " " + getName();
    }
}
