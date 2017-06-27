package ru.ssau.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.ssau.domain.Survey;
import ru.ssau.domain.UserAnswer;
import ru.ssau.exceptions.CategoryNotFoundException;
import ru.ssau.exceptions.SurveyNotFoundException;
import ru.ssau.exceptions.UserNotFoundException;
import ru.ssau.service.SurveyService;
import ru.ssau.service.UserService;
import ru.ssau.service.validation.UserRegistrationValidator;

import java.io.IOException;
import java.security.Principal;
import java.util.Locale;

@Controller
@RequestMapping( "/" )
public class WebController{

    private final UserRegistrationValidator validator;
    private final UserService               userService;
    private final MessageSource             messageSource;
    private final SurveyService             surveyService;
    private final ObjectMapper              objectMapper;

    @Autowired
    public WebController( UserRegistrationValidator validator, UserService userService, MessageSource messageSource,
                          SurveyService surveyService, ObjectMapper objectMapper ){
        this.validator = validator;
        this.userService = userService;
        this.messageSource = messageSource;
        this.surveyService = surveyService;
        this.objectMapper = objectMapper;
    }

    /***
     *
     * /***
     *
     * @param sortBy how to sort all surveys
     *               @see SurveysSort
     *               , default = by time to faster loading
     * @param limit how many surveys download , default = 5
     *
     * @param model contains List od surveys on attribute surveys
     *
     * @return index page
     */
    @RequestMapping( method = RequestMethod.GET )
    public String start( @RequestParam( required = false, defaultValue = "time" ) String sortBy,
                         @RequestParam( required = false, defaultValue = "5" ) Integer limit, Model model ){
        if( limit < 0 ) limit = 0;
        if( !sortBy.toUpperCase().equals( "USERS" ) && !sortBy.toUpperCase().equals( "TIME" ) ) sortBy = "time";
        model.addAttribute( "surveys", surveyService.getTop( sortBy, limit ) );
        return "index";
    }

    /***
     *
     * @return info page
     */
    @RequestMapping( value = "/info", method = RequestMethod.GET )
    public String info(){
        return "info";
    }

    /***
     *
     * @param error contains all errors on login form
     * @param logout status of logout of system
     * @param model contains all errors by attribute error and status of logout on attribute on msg
     * @return
     */
    @RequestMapping( value = "/login", method = RequestMethod.GET )
    public String signInPage( @RequestParam( value = "error", required = false ) String error,
                              @RequestParam( value = "logout", required = false ) String logout, Model model ){
        if( error != null ) model.addAttribute( "error", messageSource.getMessage( "login.incorrectLoginOrPassword",
                                                                                   new String[]{},
                                                                                   Locale.getDefault() ) );
        if( logout != null ) model.addAttribute( "msg", "You've been logged out successfully." );
        return "login";
    }

    /***
     *
     * @param downloadSurveys if you want too download all surveys, contains in topic
     * @param sortBy type of sorting of all topics and surveys in each topic
     * @param limit max  size of List of surveys in topic
     * @param model contains all topics on attribute topics
     * @return topics page
     */
    @RequestMapping( value = "/topics", method = RequestMethod.GET )
    public String topics( @RequestParam( required = false, defaultValue = "false" ) Boolean downloadSurveys,
                          @RequestParam( required = false, defaultValue = "time" ) String sortBy,
                          @RequestParam( required = false, defaultValue = "3" ) Integer limit, Model model ){
        if( limit < 0 ) limit = 0;
        if( !sortBy.toUpperCase().equals( "USERS" ) && !sortBy.toUpperCase().equals( "TIME" ) ) sortBy = "time";
        try{
            model.addAttribute( "topics", surveyService.getCategories( downloadSurveys,
                                                                       SurveysSort.valueOf( sortBy.toUpperCase() ),
                                                                       limit ) );
        }catch( InterruptedException e ){
            model.addAttribute( "error", "Couldn't download topics" );
        }
        return "topics";
    }

    /***
     *
     * @param name name of topic what to download
     * @param downloadSurveys if you want too download all surveys, contains in topic
     * @param sortBy type of sorting of all topics and surveys in each topic
     * @param limit max  size of List of surveys in topic
     * @param model contains topic on attribute topic
     * @return topic page
     */
    @RequestMapping( value = "/topic", method = RequestMethod.GET )
    public String topic( @RequestParam String name,
                         @RequestParam( required = false, defaultValue = "true" ) Boolean downloadSurveys,
                         @RequestParam( required = false, defaultValue = "users" ) String sortBy,
                         @RequestParam( required = false, defaultValue = "3" ) Integer limit, Model model ){
        if( limit < 0 ) limit = 0;
        if( !sortBy.toUpperCase().equals( "USERS" ) && !sortBy.toUpperCase().equals( "TIME" ) ) sortBy = "time";
        try{
            model.addAttribute( "topic", surveyService.getCategoryByName( name, downloadSurveys,
                                                                          SurveysSort.valueOf( sortBy.toUpperCase() ),
                                                                          limit ).orElseThrow(
                    () -> new CategoryNotFoundException( name ) ) );
        }catch( InterruptedException e ){
            model.addAttribute( "error", "Couldn't download topic" );
        }
        return "topic";
    }

    /***
     *
     * @param login login of user to download
     * @param model contains user on attribute user
     * @return user page
     */
    @RequestMapping( value = "/user", method = RequestMethod.GET )
    public String getUserByLogin( @RequestParam String login, Model model ){
        try{
            model.addAttribute( "user", userService.getUser( login, DeserializeUserOptions.MADESURVEYS,
                                                             DeserializeUserOptions.ANSWERS ).orElseThrow(
                    () -> new UserNotFoundException( login ) ) );
        }catch( InterruptedException e ){
            model.addAttribute( "error", "Couldn't download user" );
        }
        return "user";
    }

    /***
     *
     * @param login login of user to delete from database,
     * @param model contains error text on attribute error
     * @return redirect to logout or user page if couldn't delete
     */
    @RequestMapping( value = "/deleteUser" , method = RequestMethod.GET )
    public String deleteUserByLogin( @RequestParam String login , Model model ){
        try{
            userService.deleteUser( login );
        }catch( InterruptedException e ){
            e.printStackTrace();
            model.addAttribute( "error" , "You haven't been deleted because of too many clients" );
            return "user";
        }
        return "redirect:/logout";
    }

    /***
     *
     * if user hasn't authenticated, he can see only statistics, if authenticated but hasn't answers on this survey,
     * he can answers on this and see the statistics and his own answers
     *
     * @param id of survey to download
     * @param principal need to take user login to answer on survey
     * @param model contains survey statistics or survey on attribute survey and JSON string of survey statistics on attribute statistics
     * @return survey page
     */
    @RequestMapping( value = "/survey", method = RequestMethod.GET )
    public String survey( @RequestParam Integer id, Principal principal , Model model ){
        try{
            Survey survey = surveyService.getSurveyById( id, DeserializeSurveyOptions.CREATOR,
                                                         DeserializeSurveyOptions.QUESTIONS,
                                                         DeserializeSurveyOptions.CATEGORY,
                                                         DeserializeSurveyOptions.STATISTICS )
                    .orElseThrow( () -> new SurveyNotFoundException( id ) );
            SurveyStatistics surveyStatistics;
            if( principal != null ){
                String login = principal.getName();
                if( ! userService.hasUserAnsweredOnSurvey( new UserAnswer( survey ,
                                                                           userService.getUser( login )
                                                                                   .orElseThrow( () -> new UserNotFoundException( login ) ) ) ) ){
                    model.addAttribute( "survey", survey );
                    return "answerOnSurvey";
                }
                surveyStatistics = new SurveyStatistics( survey , login );
                model.addAttribute( "survey", surveyStatistics );
                model.addAttribute( "statistics", objectMapper.writeValueAsString( surveyStatistics ) );
                return "surveyStatistics";
            }
            surveyStatistics = new SurveyStatistics( survey );
            model.addAttribute( "survey", surveyStatistics );
            model.addAttribute( "statistics", objectMapper.writeValueAsString( surveyStatistics ) );
        }catch( InterruptedException e ){
            e.printStackTrace();
            model.addAttribute( "error", "Couldn't download survey" );
        }catch( JsonProcessingException e ){
            e.printStackTrace();
        }
        return "surveyStatistics";
    }

    /***
     *
     * @param model contains categories to choose on making survey
     * @return newSurvey Page
     */
    @RequestMapping( value = "/newSurvey", method = RequestMethod.GET )
    public String newSurvey( Model model ){
        try{
            model.addAttribute( "categories", surveyService.getCategories( false, SurveysSort.TIME, 0 ) );
        }catch( InterruptedException e ){
            e.printStackTrace();
            model.addAttribute( "error", "Couldn't download categories" );
        }
        return "newSurvey";
    }

    /***
     *
     * @param model contains user form for registrations
     * @return registration page
     */
    @RequestMapping( value = "/registration", method = RequestMethod.GET )
    public String registration( Model model ){
        model.addAttribute( "userForm", new UserRegistrationForm() );
        return "registration";
    }

    /***
     *
     * Firstly we check user's data, check duplicate of login, length of login and password and format of downloading file ,if it exists
     *
     * @param userForm container of user data,
     * @param bindingResult have all types of errors on each parameter
     * @return registrations page with errors or login page if alright
     */
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
