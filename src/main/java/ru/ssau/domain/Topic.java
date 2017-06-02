package ru.ssau.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Topic{
    @Id
    @Column( name = "name", nullable = false, length = 45 )
    private String name;

    @OneToMany
    private List<Survey> surveys;

    public String getName(){
        return name;
    }

    public void setName( String name ){
        this.name = name;
    }

    public List<Survey> getSurveys(){
        return surveys;
    }

    public void setSurveys( List<Survey> surveys ){
        this.surveys = surveys;
    }

    @Override
    public boolean equals( Object o ){
        if( this == o )
            return true;
        if( o == null || getClass() != o.getClass() )
            return false;

        Topic topic = ( Topic ) o;

        if( name != null ? !name.equals( topic.name ) : topic.name != null )
            return false;

        return true;
    }

    @Override
    public int hashCode(){
        return name != null ? name.hashCode() : 0;
    }
}
