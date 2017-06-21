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
import ru.ssau.exceptions.CategoryNotFoundException;
import ru.ssau.exceptions.SurveyNotFoundException;
import ru.ssau.exceptions.UserNotFoundException;
import ru.ssau.service.SurveyService;
import ru.ssau.service.UserService;
import ru.ssau.service.validation.UserRegistrationValidator;

import java.io.IOException;
import java.util.Locale;

@Controller
@RequestMapping( "/" )
public class WebController{

    private final UserRegistrationValidator validator;
    private final UserService               userService;
    private final MessageSource             messageSource;
    private final SurveyService             surveyService;

    @Autowired
    public WebController( UserRegistrationValidator validator, UserService userService, MessageSource messageSource,
                          SurveyService surveyService ){
        this.validator = validator;
        this.userService = userService;
        this.messageSource = messageSource;
        this.surveyService = surveyService;
    }

    @RequestMapping( method = RequestMethod.GET )
    public String start( @RequestParam( required = false, defaultValue = "time" ) String sortBy,
                         @RequestParam( required = false, defaultValue = "5" ) Integer limit, Model model ){
        if( limit < 0 )
            limit = 0;
        if( !sortBy.toUpperCase().equals( "USERS" ) && !sortBy.toUpperCase().equals( "TIME" ) ) sortBy = "time";
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
    public String topics( @RequestParam( required = false, defaultValue = "false" ) Boolean downloadSurveys,
                          @RequestParam( required = false, defaultValue = "time" ) String sortBy,
                          @RequestParam( required = false, defaultValue = "3" ) Integer limit, Model model ){
        if( limit < 0 )
            limit = 0;
        if( !sortBy.toUpperCase().equals( "USERS" ) && !sortBy.toUpperCase().equals( "TIME" ) ) sortBy = "time";
        try{
            model.addAttribute( "topics",
                                surveyService.getCategories( downloadSurveys, SurveysSort.valueOf( sortBy.toUpperCase() ), limit ) );
        }catch( InterruptedException e ){
            model.addAttribute( "error" , "Couldn't download topics" );
        }
        return "topics";
    }

    @RequestMapping( value = "/topic", method = RequestMethod.GET )
    public String topic( @RequestParam String name,
                         @RequestParam( required = false, defaultValue = "true" ) Boolean downloadSurveys,
                         @RequestParam( required = false, defaultValue = "users" ) String sortBy,
                         @RequestParam( required = false, defaultValue = "3" ) Integer limit, Model model ){
        if( limit < 0 )
            limit = 0;
        if( !sortBy.toUpperCase().equals( "USERS" ) && !sortBy.toUpperCase().equals( "TIME" ) ) sortBy = "time";
        try{
            model.addAttribute( "topic",
                                surveyService.getCategoryByName( name, downloadSurveys, SurveysSort.valueOf( sortBy.toUpperCase() ),
                                                                 limit ).orElseThrow( () -> new CategoryNotFoundException( name ) ) );
        }catch( InterruptedException e ){
            model.addAttribute( "error" , "Couldn't download topic" );
        }
        return "topic";
    }

    @RequestMapping( value = "/user", method = RequestMethod.GET )
    public String getUserByLogin( @RequestParam String login, Model model ){
        try{
            model.addAttribute( "user", userService.getUser( login, DeserializeUserOptions.MADESURVEYS , DeserializeUserOptions.ANSWERS )
                    .orElseThrow( () -> new UserNotFoundException( login ) ) );
        }catch( InterruptedException e ){
            model.addAttribute( "error" , "Couldn't download user" );
        }
        return "user";
    }

    @RequestMapping( value = "/survey", method = RequestMethod.GET )
    public String survey( @RequestParam Integer id, Model model ){
        try{
            model.addAttribute( "survey", surveyService.getSurveyById( id, DeserializeSurveyOptions.CREATOR,
                                                                       DeserializeSurveyOptions.QUESTIONS,
                                                                       DeserializeSurveyOptions.CATEGORY )
                    .orElseThrow( () -> new SurveyNotFoundException( id ) ) );
        }catch( InterruptedException e ){
            e.printStackTrace();
            model.addAttribute( "error" , "Couldn't download survey" );
        }
        return "survey";
    }

    @RequestMapping( value = "/newSurvey", method = RequestMethod.GET )
    public String newSurvey( Model model ){
        try{
            model.addAttribute( "categories" , surveyService.getCategories( false , SurveysSort.TIME , 0 ) );
        }catch( InterruptedException e ){
            e.printStackTrace();
            model.addAttribute( "error" , "Couldn't download categories" );
        }
        return "newSurvey";
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
        }catch( InterruptedException e ){
            e.printStackTrace();
        }
        return "redirect:/login";
    }
}
