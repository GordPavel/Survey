package ru.ssau.DAO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.ssau.domain.Category;
import ru.ssau.domain.Survey;

import java.util.List;

public class BDCategory{

    private String       name;
    @JsonIgnore
    private List<Survey> surveys;

    public BDCategory(){
    }

    public BDCategory( Category category ){
        this.name = category.getName();
    }

    public String getName(){
        return name;
    }

    public void setName( String name ){
        this.name = name;
    }

    public void setSurveys( List<Survey> surveys ){
        this.surveys = surveys;
    }

    public Category toCategory(){
        Category category = new Category();
        category.setName( this.name );
        category.setSurveys( this.surveys );
        return category;
    }
}
