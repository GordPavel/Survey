package ru.ssau.service.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.ssau.domain.Survey;
import ru.ssau.service.SurveyService;

@Component
public class NewSurveyValidator implements Validator{

    @Autowired
    private SurveyService surveyService;

    @Override
    public boolean supports( Class<?> aClass ){
        return Survey.class.equals( aClass );
    }

    @Override
    public void validate( Object o, Errors errors ){
        // TODO: 21.06.17
    }
}
