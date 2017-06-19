package ru.ssau.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ssau.DAO.enums.DeserializeSurveyOptions;
import ru.ssau.DAO.enums.DeserializeUserOptions;
import ru.ssau.DAO.enums.SurveysSort;
import ru.ssau.service.SurveyService;
import ru.ssau.service.UserService;
import ru.ssau.service.validation.UserRegistrationValidator;

import java.io.IOException;
import java.util.Locale;

@Controller
@RequestMapping( "/" )
public class WebController{

    @Autowired
    private UserRegistrationValidator validator;
    @Autowired
    private UserService               userService;
    @Autowired
    private MessageSource             messageSource;
    @Autowired
    private SurveyService             surveyService;

    @RequestMapping( method = RequestMethod.GET )
    public String start( @RequestParam( required = false, defaultValue = "time" ) String sortBy,
                         @RequestParam( required = false, defaultValue = "5" ) Integer limit, Model model ){
        if( limit < 0 )
            limit = 0;
        if( !sortBy.equals( "users" ) && !sortBy.equals( "time" ) )
            sortBy = "time";
        model.addAttribute( "surveys", surveyService.getTop( sortBy, limit ) );
        return "index";
    }

    @RequestMapping( value = "/info", method = RequestMethod.GET )
    public String info(){
        return "info";
    }

    @RequestMapping( value = "/login", method = RequestMethod.GET )
    public String signInPage( @RequestParam( value = "error", required = false ) String error,
                              @RequestParam( value = "logout", required = false ) String logout, Model model ){
        if( error != null ) model.addAttribute( "error", messageSource.getMessage( "login.incorrectLoginOrPassword",
                                                                                   new String[]{},
                                                                                   Locale.getDefault() ) );
        if( logout != null ) model.addAttribute( "msg", "You've been logged out successfully." );
        return "login";
    }

    @RequestMapping( value = "/topics", method = RequestMethod.GET )
    public String topics( @RequestParam( required = false, defaultValue = "true" ) Boolean downloadSurveys,
                          @RequestParam( required = false, defaultValue = "users" ) String sortBy,
                          @RequestParam( required = false, defaultValue = "3" ) Integer limit, Model model ){
        if( limit < 0 )
            limit = 0;
        if( !sortBy.equals( "users" ) || ! sortBy.equals( "time" ) )
            sortBy = "users";
        model.addAttribute( "topics",
                            surveyService.getCategories( downloadSurveys, SurveysSort.valueOf( sortBy ), limit ) );
        return "topics";
    }

    @RequestMapping( value = "/topic", method = RequestMethod.GET )
    public String topic( @RequestParam String name,
                         @RequestParam( required = false, defaultValue = "true" ) Boolean downloadSurveys,
                         @RequestParam( required = false, defaultValue = "users" ) String sortBy,
                         @RequestParam( required = false, defaultValue = "3" ) Integer limit, Model model ){
        if( limit < 0 )
            limit = 0;
        if( !sortBy.equals( "users" ) || ! sortBy.equals( "time" ) )
            sortBy = "users";
        model.addAttribute( "topic",
                            surveyService.getCategoryByName( name, downloadSurveys, SurveysSort.valueOf( sortBy ),
                                                             limit ) );
        return "topic";
    }

    @RequestMapping( value = "/user", method = RequestMethod.GET )
    public String getUserByLogin( @RequestParam String login, Model model,
                                  @RequestParam DeserializeUserOptions... options ){
        model.addAttribute( "topic", userService.getUser( login, options ) );
        return "user";
    }

    @RequestMapping( value = "/survey", method = RequestMethod.GET )
    public String survey( @RequestParam Integer id, Model model,
                          @RequestParam( required = false, defaultValue = "false" ) Boolean downloadAnswers,
                          @RequestParam DeserializeSurveyOptions... options ){
        model.addAttribute( "survey", surveyService.getSurveyById( id, downloadAnswers, options ) );
        return "survey";
    }

//    @RequestMapping( value = "/search" , method = RequestMethod.GET )
//    public String search( @RequestParam String search ){
//
//    }

    @RequestMapping( value = "/registration", method = RequestMethod.GET )
    public String registration( Model model ){
        model.addAttribute( "userForm", new UserRegistrationForm() );
        return "registration";
    }

    @RequestMapping( value = "/registration", method = RequestMethod.POST )
    public String registration( @ModelAttribute( "userForm" ) UserRegistrationForm userForm,
                                BindingResult bindingResult ){
        validator.validate( userForm, bindingResult );
        if( bindingResult.hasErrors() ) return "registration";
        try{
            userService.saveUser( userForm );
        }catch( IOException e ){
            bindingResult.addError( new FieldError( "userForm", "file",
                                                    messageSource.getMessage( "File.UploadError", new String[]{},
                                                                              Locale.getDefault() ) ) );
            return "registration";
        }
        return "redirect:/";
    }
}
