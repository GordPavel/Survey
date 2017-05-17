package ru.ssau.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;
import ru.ssau.exceptions.SurveyNotFoundException;
import ru.ssau.service.SurveyService;
import ru.ssau.service.UserService;
import ru.ssau.service.filesmanager.FilesManager;
import ru.ssau.service.filesmanager.MyFile;
import ru.ssau.service.validation.UserRegistrationValidator;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping( value = "/client" )
public class ClientController{

    @Autowired
    private SurveyService surveyService;
    @Autowired
    private UserService               userService;
    @Autowired
    private FilesManager              filesManager;
    @Autowired
    private UserRegistrationValidator validator;


    @RequestMapping( value = "/topSurveys", method = RequestMethod.GET )
    public List<Survey> topSurveys(){
        List<Survey> surveys = surveyService.getTop();
        surveys.sort( Comparator.comparingInt( Survey::getUsersDone ) );
        return surveys;
    }

    @RequestMapping( value = "/surveyId={id}", method = RequestMethod.GET )
    public Survey getSurveyById( @PathVariable Integer id ){
        return surveyService.getSurveyById( id ).orElseThrow( () -> new SurveyNotFoundException( id ) );
    }

    @RequestMapping( value = "/userLogin={login}", method = RequestMethod.GET )
    public User getUserByLogin( @PathVariable String login ){
        return userService.getUser( login ).orElseThrow( () -> new UsernameNotFoundException( login ) );
    }

    @RequestMapping( value = "/registration", method = RequestMethod.POST )
    public boolean newUser( @ModelAttribute( "user" ) User user ){
        if( validator.validate( user ) ){
            userService.saveUser( user );
            return true;
        }else
            return false;
    }

    @RequestMapping( value = "/survey/userLogin={login}", method = RequestMethod.GET )
    public List<Survey> getListSurveyByUserMadeLogin( @PathVariable String login ){
        return getUserByLogin( login ).getMadeSurveys();
    }

    @RequestMapping( value = "/img={id}", method = RequestMethod.GET )
    public ResponseEntity<byte[]> getImage( @PathVariable String id ){
        try{
            MyFile            file    = filesManager.getFile( id );
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType( file.getMediaType() );
            return new ResponseEntity<>( file.getBytes(), headers, HttpStatus.CREATED );
        }catch( IllegalArgumentException | IOException e ){
            System.out.println( String.format( "Ошибка чтения файла %s%s.jpeg", filesManager.getFilesDir(), id ) );
            return new ResponseEntity<>( null, null, HttpStatus.NOT_FOUND );
        }
    }
}

