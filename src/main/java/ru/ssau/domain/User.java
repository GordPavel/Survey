package ru.ssau.domain;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class User{
    @Id
    @Column( name = "login", nullable = false, length = 32, unique = true )
    private String       login;
    @Basic
    @Column( name = "password", nullable = false, length = 256 )
    private String       password;
    @Basic
    @Column( name = "name", length = 45 )
    private String       name;
    @Basic
    @Column( name = "lastName", length = 45 )
    private String       lastName;
    @Enumerated( EnumType.STRING )
    @Column( name = "Role_name" )
    private Role         userRole;
    @OneToMany( fetch = FetchType.EAGER, mappedBy = "creator", cascade = CascadeType.ALL )
    private List<Survey> surveysMade;

    @Transient
    private List<Survey> surveysDone;

    public String getLogin(){
        return login;
    }

    public void setLogin( String login ){
        this.login = login;
    }

    public String getPassword(){
        return password;
    }

    public String getName(){
        return name;
    }

    public void setPassword( String password ){
        this.password = password;
    }

    public void setName( String name ){
        this.name = name;
    }

    public String getLastName(){
        return lastName;
    }

    public void setLastName( String lastName ){
        this.lastName = lastName;
    }

    public List<Survey> getSurveysMade(){
        return surveysMade;
    }

    public void setSurveysMade( List<Survey> surveysMade ){
        this.surveysMade = surveysMade;
    }

    public List<Survey> getSurveysDone(){
        return surveysDone;
    }

    public void setSurveysDone( List<UserAnswer> surveysDone ){
        this.surveysDone = surveysDone.stream().map( UserAnswer::getSurvey ).collect( Collectors.toList() );
    }

    @Override
    public boolean equals( Object o ){
        if( this == o )
            return true;
        if( o == null || getClass() != o.getClass() )
            return false;

        User user = ( User ) o;

        if( login != null ? !login.equals( user.login ) : user.login != null )
            return false;
        if( password != null ? !password.equals( user.password ) : user.password != null )
            return false;
        if( name != null ? !name.equals( user.name ) : user.name != null )
            return false;
        if( lastName != null ? !lastName.equals( user.lastName ) : user.lastName != null )
            return false;

        return true;
    }

    @Override
    public int hashCode(){
        int result = login != null ? login.hashCode() : 0;
        result = 31 * result + ( password != null ? password.hashCode() : 0 );
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        result = 31 * result + ( lastName != null ? lastName.hashCode() : 0 );
        return result;
    }

    public Role getUserRole(){
        return userRole;
    }

    public void setUserRole( Role roleByRoleName ){
        this.userRole = roleByRoleName;
    }
}
