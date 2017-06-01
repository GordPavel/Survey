package ru.ssau.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.ssau.domain.Category;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;
import ru.ssau.exceptions.SurveyNotFoundException;
import ru.ssau.service.SurveyService;
import ru.ssau.service.UserService;
import ru.ssau.service.filesmanager.FilesManager;
import ru.ssau.service.filesmanager.MyFile;
import ru.ssau.service.validation.UserRegistrationValidator;
import ru.ssau.transport.SurveyTransport;
import ru.ssau.transport.TopicTransport;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
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
    public List<SurveyTransport> topSurveys( @RequestParam( required = false, defaultValue = "users" ) String sortBy,
                                             @RequestParam( required = false, defaultValue = "5" ) Integer limit ){
        if( sortBy.equals( "users" ) )
            return surveyService.getTop().stream().sorted( Comparator.comparingInt( Survey::getUsersDone ) ).limit(
                    limit ).map( survey -> new SurveyTransport( survey.getId(), survey.getName() ) ).collect(
                    Collectors.toList() );
        else
            return surveyService.getTop().stream().sorted(
                    Comparator.comparingLong( ( survey ) -> survey.getDate().getTime() ) ).limit( limit ).map(
                    survey -> new SurveyTransport( survey.getId(), survey.getName() ) ).collect( Collectors.toList() );
    }

    @RequestMapping( value = "/survey", method = RequestMethod.GET )
    public ResponseEntity<?> getSurveyById( @RequestParam Integer id ){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_JSON_UTF8 );
        return ResponseEntity.accepted().headers( headers ).body(
                surveyService.getSurveyById( id ).orElseThrow( () -> new SurveyNotFoundException( id ) ) );
    }

    @RequestMapping( value = "/user", method = RequestMethod.GET )
    public ResponseEntity<?> getUserByLogin( @RequestParam String login ){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_JSON_UTF8 );
        return ResponseEntity.accepted().headers( headers ).body(
                userService.getUser( login ).orElseThrow( () -> new UsernameNotFoundException( login ) ) );
    }

    @RequestMapping( value = "/login", method = RequestMethod.POST )
    public ResponseEntity<?> login( @RequestParam String login, @RequestParam String password ){
        Optional<User> userOptional = userService.getUser( login );
        if( !userOptional.isPresent() )
            return ResponseEntity.notFound().build();
        User user = userOptional.get();
        if( !user.getPassword().equals( passwordEncoder.encodePassword( password, null ) ) )
            return ResponseEntity.badRequest().build();
        return ResponseEntity.status( HttpStatus.OK ).contentType( MediaType.APPLICATION_JSON_UTF8 ).body( user );
    }

    @RequestMapping( value = "/registration", method = RequestMethod.POST )
    public ResponseEntity<?> newUser( @RequestParam( "newUser" ) String JSONUser ) throws IOException{
        User user = new ObjectMapper().readValue( JSONUser, User.class );
        if( validator.validate( user ) ){
            userService.saveUser( user );
            return ResponseEntity.ok().build();
        }else
            return ResponseEntity.badRequest().build();
    }

    @RequestMapping( value = "/topics", method = RequestMethod.GET )
    public ResponseEntity<?> topics( @RequestParam( required = false, defaultValue = "users" ) String sortBy,
                                     @RequestParam( required = false, defaultValue = "3" ) Integer limit ){
        List<TopicTransport> list;
        if( sortBy.equals( "users" ) )
            list = surveyService.getCategories().stream().map( category -> new TopicTransport( category.getName(),
                                                                                               category.getSurveys().stream().sorted(
                                                                                                       Comparator.comparingInt(
                                                                                                               Survey::getUsersDone ) ).limit(
                                                                                                       limit ).map(
                                                                                                       survey -> new SurveyTransport(
                                                                                                               survey.getId(),
                                                                                                               survey.getName() ) ).collect(
                                                                                                       Collectors.toList() ) ) ).collect(
                    Collectors.toList() );
        else{
            list = surveyService.getCategories().stream().map( category -> new TopicTransport( category.getName(),
                                                                                               category.getSurveys().stream().sorted(
                                                                                                       Comparator.comparingLong(
                                                                                                               survey -> survey.getDate().getTime() ) ).limit(
                                                                                                       limit ).map(
                                                                                                       survey -> new SurveyTransport(
                                                                                                               survey.getId(),
                                                                                                               survey.getName() ) ).collect(
                                                                                                       Collectors.toList() ) ) ).collect(
                    Collectors.toList() );
        }
        return ResponseEntity.status( HttpStatus.OK ).contentType( MediaType.APPLICATION_JSON_UTF8 ).body( list );
    }

    @RequestMapping( value = "/topic", method = RequestMethod.GET )
    public ResponseEntity<?> getTopic( @RequestParam String name ){
        Optional<Category> optional = surveyService.getCategoryByName( name );
        return optional.<ResponseEntity<?>>map(
                category -> ResponseEntity.status( HttpStatus.OK ).contentType( MediaType.APPLICATION_JSON_UTF8 ).body(
                        new TopicTransport( category.getName(), category.getSurveys().stream().map(
                                survey -> new SurveyTransport( survey.getId(), survey.getName() ) ).collect(
                                Collectors.toList() ) ) ) ).orElseGet( () -> ResponseEntity.notFound().build() );
    }


    @RequestMapping( value = "/doneSurvey", method = RequestMethod.POST )
    public void doneSurvey( @RequestParam String answers, @RequestParam Integer id, @RequestParam String login ) throws
                                                                                                                 IOException{
        List<Integer> listAnswers = new ObjectMapper().readValue( answers, new TypeReference<List<Integer>>(){
        } );
        Survey survey = surveyService.getSurveyById( id ).get();
        User   user   = userService.getUser( login ).get();
        return;
    }

    @RequestMapping( value = "/createdSurvey", method = RequestMethod.POST )
    public void newSurvey( @RequestParam String createdSurvey ) throws IOException{
        Survey survey = null;
        try{
            survey = new ObjectMapper().readValue( createdSurvey, Survey.class );
        }catch( JsonParseException | JsonMappingException e ){
            e.printStackTrace();
        }
        survey.setDate( new Date() );
        return;
    }

    @RequestMapping( value = "/img", method = RequestMethod.GET )
    public ResponseEntity<?> getImage( @RequestParam String id ) throws IOException{
        MyFile file = filesManager.getFile( id + ".png" );
        return ResponseEntity.accepted().headers( file.getHeaders() ).body( file.getBytes() );
    }

    @RequestMapping( value = "/img/upload", method = RequestMethod.POST )
    public ResponseEntity<?> uploadPic( @RequestParam( value = "profile_picture" ) MultipartFile file,
                                        @RequestParam( value = "userLogin" ) String userLogin ) throws IOException{
        filesManager.saveFile( file.getBytes(), userLogin );
        return ResponseEntity.ok().build();
    }

    @RequestMapping( value = "/img/delete", method = RequestMethod.DELETE )
    public ResponseEntity<?> deletePic( @RequestParam( value = "login" ) String location ) throws IOException{
        filesManager.deleteFile( location );
        return ResponseEntity.ok().build();
    }
}

