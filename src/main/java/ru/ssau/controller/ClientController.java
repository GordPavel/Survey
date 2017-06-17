package ru.ssau.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.ssau.DAO.survey.DeserializeSurveyOptions;
import ru.ssau.domain.Survey;
import ru.ssau.service.SurveyService;
import ru.ssau.service.UserService;
import ru.ssau.service.filesmanager.FilesManager;
import ru.ssau.service.filesmanager.MyFile;
import ru.ssau.service.validation.UserRegistrationValidator;

import java.io.IOException;
import java.util.List;

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
    @Autowired
    private ObjectMapper              objectMapper;

    @RequestMapping( value = "/topSurveys", method = RequestMethod.GET, headers = "Accept=application/json" )
    public List<Survey> topSurveys( @RequestParam( required = false, defaultValue = "users" ) String sortBy,
                                    @RequestParam( required = false, defaultValue = "5" ) Integer limit,
                                    @RequestParam( required = false, value = "[]" ) String... deserializeOptions ){
        return surveyService.getTop( sortBy, limit, DeserializeSurveyOptions.fromStrings( deserializeOptions ) );
    }

    @RequestMapping( value = "/survey", method = RequestMethod.GET )
    public ResponseEntity<?> getSurveyById( @RequestParam Integer id ) throws JsonProcessingException{
        // TODO: 16.06.17
        return null;
    }

    @RequestMapping( value = "/user", method = RequestMethod.GET )
    public ResponseEntity<?> getUserByLogin( @RequestParam String login ){
        // TODO: 16.06.17
        return null;
    }

    @RequestMapping( value = { "/user/doneSurveys" }, method = { RequestMethod.GET } )
    public ResponseEntity<?> getDoneSurveysByLogin( @RequestParam String login ) throws JsonProcessingException{
        // TODO: 16.06.17
        return null;
    }

    @RequestMapping( value = { "/user/madeSurveys" }, method = { RequestMethod.GET } )
    public ResponseEntity<?> getMadeSurveysByLogin( @RequestParam String login ) throws JsonProcessingException{
        // TODO: 16.06.17
        return null;
    }

    @RequestMapping( value = "/login", method = RequestMethod.POST )
    public ResponseEntity<?> login( @RequestParam String login, @RequestParam String password ){
        // TODO: 16.06.17
        return null;
    }

    @RequestMapping( value = "/registration", method = RequestMethod.POST )
    public ResponseEntity<?> newUser( @RequestParam( "newUser" ) String JSONUser ) throws IOException{
        // TODO: 16.06.17
        return null;
    }

    @RequestMapping( value = "/topics", method = RequestMethod.GET )
    public ResponseEntity<?> topics( @RequestParam( required = false, defaultValue = "users" ) String sortBy,
                                     @RequestParam( required = false, defaultValue = "3" ) Integer limit ){
        // TODO: 16.06.17
        return null;
    }

    @RequestMapping( value = "/topic", method = RequestMethod.GET )
    public ResponseEntity<?> getTopic( @RequestParam String name ){
        // TODO: 16.06.17
        return null;
    }


    @RequestMapping( value = "/doneSurvey", method = RequestMethod.POST )
    public void doneSurvey( @RequestParam String answers, @RequestParam Integer id, @RequestParam String login )
            throws IOException{
        // TODO: 16.06.17
        return;
    }

    @RequestMapping( value = "/createdSurvey", method = RequestMethod.POST )
    public void newSurvey( @RequestParam String createdSurvey ) throws IOException{
        // TODO: 16.06.17
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

