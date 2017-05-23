package ru.ssau.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
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
import ru.ssau.transport.SurveyTransport;
import ru.ssau.transport.UserRegistrationForm;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping( value = "/client" )
public class ClientController{

    @Autowired
    private SurveyService             surveyService;
    @Autowired
    private UserService               userService;
    @Autowired
    private FilesManager              filesManager;
    @Autowired
    private UserRegistrationValidator validator;
    @Autowired
    private ShaPasswordEncoder        passwordEncoder;


    @RequestMapping( value = "/topSurveys", method = RequestMethod.GET, headers = "Accept=application/json" )
    public List<SurveyTransport> topSurveys(){
        return surveyService.getTop().stream().map(
                survey -> new SurveyTransport( survey.getId(), survey.getName() ) ).collect( Collectors.toList() );
    }

    @RequestMapping( value = "/survey", method = RequestMethod.GET )
    public Survey getSurveyById( @RequestParam Integer id ){
        return surveyService.getSurveyById( id ).orElseThrow( () -> new SurveyNotFoundException( id ) );
    }

    @RequestMapping( value = "/user/surveys", method = RequestMethod.GET )
    public List<Survey> getListSurveyByUserMadeLogin( @RequestParam String userLogin ){
        return getUserByLogin( userLogin ).getMadeSurveys();
    }

    @RequestMapping( value = "/user", method = RequestMethod.GET )
    public User getUserByLogin( @RequestParam String login ){
        return userService.getUser( login ).orElseThrow( () -> new UsernameNotFoundException( login ) );
    }

    @RequestMapping( value = "/login", method = RequestMethod.POST )
    public ResponseEntity<?> login( @RequestParam( defaultValue = "" ) String login,
                                    @RequestParam( defaultValue = "" ) String password ){
        Optional<User> userOptional = userService.getUser( login );
        if( !userOptional.isPresent() )
            return ResponseEntity.notFound().build();
        User user = userOptional.get();
        if( !user.getPassword().equals( passwordEncoder.encodePassword( password, null ) ) )
            return ResponseEntity.badRequest().build();
        return ResponseEntity.status( HttpStatus.OK ).contentType( MediaType.APPLICATION_JSON_UTF8 ).body( "welcome" );
    }

    @RequestMapping( value = "/registration", method = RequestMethod.POST )
    public boolean newUser( @ModelAttribute( "user" ) UserRegistrationForm user ){
        if( validator.validate( user ) ){
            userService.saveUser( user );
            return true;
        }else
            return false;
    }

    @RequestMapping( value = "/img", method = RequestMethod.GET )
    public ResponseEntity<?> getImage( @RequestParam String id ){
        try{
            MyFile file = filesManager.getFile( id );
            return ResponseEntity.status( HttpStatus.CREATED ).body( file.getBytes() );
        }catch( IllegalArgumentException | IOException e ){
            System.out.println( String.format( "Ошибка чтения файла %s%s.jpeg", filesManager.getFilesDir(), id ) );
            return ResponseEntity.notFound().build();
        }
    }
}

