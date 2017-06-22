package ru.ssau.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.ssau.DAO.enums.DeserializeSurveyOptions;
import ru.ssau.DAO.enums.DeserializeUserOptions;
import ru.ssau.DAO.enums.SurveysSort;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;
import ru.ssau.domain.UserAnswer;
import ru.ssau.exceptions.SurveyAlreadyDoneByUserException;
import ru.ssau.service.SurveyService;
import ru.ssau.service.UserService;
import ru.ssau.service.filesmanager.FilesManager;
import ru.ssau.service.filesmanager.MyFile;
import ru.ssau.service.validation.NewUserAnswerValidator;
import ru.ssau.service.validation.UserRegistrationValidator;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping( value = "/client" )
public class ClientController{
    private final SurveyService             surveyService;
    private final UserService               userService;
    private final FilesManager              filesManager;
    private final UserRegistrationValidator validator;
    private final ShaPasswordEncoder        passwordEncoder;
    private final ObjectMapper              objectMapper;
    private final MessageSource             messageSource;
    private final NewUserAnswerValidator    userAnswerValidator;


    @Autowired
    public ClientController( SurveyService surveyService, UserService userService, FilesManager filesManager,
                             UserRegistrationValidator validator, ShaPasswordEncoder passwordEncoder,
                             ObjectMapper objectMapper, @Qualifier( "messageSource" ) MessageSource messageSource,
                             NewUserAnswerValidator userAnswerValidator ){
        this.surveyService = surveyService;
        this.userService = userService;
        this.filesManager = filesManager;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
        this.userAnswerValidator = userAnswerValidator;
    }

    //   Проверено
    @RequestMapping( value = "/topSurveys", method = RequestMethod.GET, headers = "Accept=application/json" )
    public List<Survey> topSurveys( @RequestParam( required = false, defaultValue = "time" ) String sortBy,
                                    @RequestParam( required = false, defaultValue = "5" ) Integer limit,
                                    @RequestParam( required = false, defaultValue = "[]" ) String options ){
        if( limit < 0 ) limit = 0;
        if( !sortBy.toUpperCase().equals( "USERS" ) && !sortBy.toUpperCase().equals( "TIME" ) ) sortBy = "time";
        try{
            DeserializeSurveyOptions[] deserializeOptions = objectMapper.readValue( options,
                                                                                    DeserializeSurveyOptions[].class );
            return surveyService.getTop( sortBy, limit, deserializeOptions );
        }catch( IOException e ){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    //   Проверено
    @RequestMapping( value = "/survey", method = RequestMethod.GET )
    public ResponseEntity<?> getSurveyById( @RequestParam Integer id,
                                            @RequestParam( required = false, defaultValue = "[]" ) String options )
            throws JsonProcessingException{
        try{
            if( id == null ) return ResponseEntity.status( HttpStatus.BAD_REQUEST ).build();
            DeserializeSurveyOptions[] deserializeOptions = objectMapper.readValue( options,
                                                                                    DeserializeSurveyOptions[].class );
            return surveyService.getSurveyById( id, deserializeOptions ).<ResponseEntity<?>>map(
                    survey -> ResponseEntity.ok().contentType( MediaType.APPLICATION_JSON_UTF8 ).body(
                            survey ) ).orElseGet( () -> ResponseEntity.notFound().build() );
        }catch( InterruptedException e ){
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.TOO_MANY_REQUESTS ).build();
        }catch( IOException e ){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    //   Проверено
    @RequestMapping( value = "/user", method = RequestMethod.GET )
    public ResponseEntity<?> getUserByLogin( @RequestParam String login,
                                             @RequestParam( required = false, value = "[]" ) String options ){
        try{
            DeserializeUserOptions[] deserializeUserOptions = objectMapper.readValue( options,
                                                                                      DeserializeUserOptions[].class );
            return userService.getUser( login, deserializeUserOptions ).<ResponseEntity<?>>map(
                    user -> ResponseEntity.ok().contentType( MediaType.APPLICATION_JSON_UTF8 ).body( user ) ).orElseGet(
                    () -> ResponseEntity.notFound().build() );
        }catch( InterruptedException e ){
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.TOO_MANY_REQUESTS ).build();
        }catch( IOException e ){
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.BAD_REQUEST ).build();
        }
    }

    @RequestMapping( value = "/user", method = RequestMethod.DELETE )
    public ResponseEntity<?> deleteUserByLogin( @RequestParam String login ){
        try{
            userService.deleteUser( login );
            return ResponseEntity.ok().build();
        }catch( InterruptedException e ){
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.TOO_MANY_REQUESTS ).build();
        }
    }

//    Проверено
    @RequestMapping( value = "/user/doneSurveys" , method = { RequestMethod.GET } )
    public ResponseEntity<?> getDoneSurveysByLogin( @RequestParam String login ){
        try{
            return ResponseEntity.ok().contentType( MediaType.APPLICATION_JSON_UTF8 ).body(
                    userService.getDoneSurveysByLogin( login ) );
        }catch( InterruptedException e ){
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.TOO_MANY_REQUESTS ).build();
        }
    }


//    Проверено
    @RequestMapping( value = "/user/madeSurveys" , method = { RequestMethod.GET } )
    public ResponseEntity<?> getMadeSurveysByLogin( @RequestParam String login ){
        try{
            return ResponseEntity.ok().contentType( MediaType.APPLICATION_JSON_UTF8 ).body(
                    userService.getMadeSurveysByLogin( login ) );
        }catch( InterruptedException e ){
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.TOO_MANY_REQUESTS ).build();
        }
    }

    //    Проверено
    @RequestMapping( value = "/user/changeName" , method = RequestMethod.POST )
    public ResponseEntity<?> changeUserName( @RequestParam String login , @RequestParam String newName ){
        userService.changeUserName( login , newName );
        return ResponseEntity.ok().build();
    }
    //    Проверено
    @RequestMapping( value = "/user/changeSurname" , method = RequestMethod.POST )
    public ResponseEntity<?> changeUseLastName( @RequestParam String login , @RequestParam String newSurname ){
        userService.changeUserLastName( login , newSurname );
        return ResponseEntity.ok().build();
    }

    //   Проверено
    @RequestMapping( value = "/login", method = RequestMethod.POST )
    public ResponseEntity<?> login( @RequestParam String login, @RequestParam String password,
                                    @RequestParam( required = false, defaultValue = "[]" ) String options ){
        try{
            DeserializeUserOptions[] deserializeUserOptions = objectMapper.readValue( options,
                                                                                      DeserializeUserOptions[].class );
            Optional<User>           user                   = userService.getUser( login, deserializeUserOptions );
            if( user.isPresent() ){
                if( user.get().getPassword().equals( passwordEncoder.encodePassword( password, null ) ) )
                    return ResponseEntity.ok().contentType( MediaType.APPLICATION_JSON_UTF8 ).body( user.get() );
                else{return ResponseEntity.badRequest().build();}
            }else return ResponseEntity.notFound().build();
        }catch( InterruptedException e ){
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.TOO_MANY_REQUESTS ).build();
        }catch( IOException e ){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    //   Проверено
    @RequestMapping( value = "/registration", method = RequestMethod.POST )
    public ResponseEntity<?> newUser( @RequestParam( "newUser" ) String JSONUser ){
        try{
            User user = objectMapper.readValue( JSONUser, User.class );
            switch( validator.validate( user ) ){
                case 0:
                    userService.saveUser( user );
                    return ResponseEntity.ok().build();
                case 1:
                    return ResponseEntity.badRequest().contentType( MediaType.APPLICATION_JSON_UTF8 ).body(
                            "Логин занят" );
                case 2:
                    return ResponseEntity.badRequest().contentType( MediaType.APPLICATION_JSON_UTF8 ).body(
                            "Логин должен быть больше 6 и меньше 32 символов" );
                case 3:
                    return ResponseEntity.badRequest().contentType( MediaType.APPLICATION_JSON_UTF8 ).body(
                            "Пароль должен быть больше 6 и меньше 32 символов" );
                default:
                    return ResponseEntity.badRequest().build();
            }
        }catch( InterruptedException e ){
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.TOO_MANY_REQUESTS ).build();
        }catch( IOException e ){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    //   Проверено
    @RequestMapping( value = "/topics", method = RequestMethod.GET )
    public ResponseEntity<?> topics( @RequestParam( required = false, defaultValue = "true" ) Boolean downloadSurveys,
                                     @RequestParam( required = false, defaultValue = "time" ) String sortBy,
                                     @RequestParam( required = false, defaultValue = "3" ) Integer limit ){
        if( limit < 0 ) limit = 0;
        if( !sortBy.toUpperCase().equals( "USERS" ) || !sortBy.toUpperCase().equals( "TIME" ) ) sortBy = "TIME";
        try{
            return ResponseEntity.ok().contentType( MediaType.APPLICATION_JSON_UTF8 ).body(
                    surveyService.getCategories( downloadSurveys, SurveysSort.valueOf( sortBy.toUpperCase() ), limit ) );
        }catch( InterruptedException e ){
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.TOO_MANY_REQUESTS ).build();
        }
    }
    // Проверено
    @RequestMapping( value = "/doneSurvey", method = RequestMethod.POST )
    public ResponseEntity<?> doneSurvey( @RequestParam String answers, @RequestParam Integer id,
                                         @RequestParam String login ){
        try{
            UserAnswer userAnswer = new UserAnswer();
            userAnswer.setSurvey( surveyService.getSurveyById( id ).get() );
            userAnswer.setUser( userService.getUser( login ).get() );
            List<Integer> list = objectMapper.readValue( answers, new TypeReference<List<Integer>>(){} );
            userAnswer.setAnswers( list );
            switch( userAnswerValidator.validate( userAnswer ) ){
                case 0:
                    userService.saveNewUserAnswer( userAnswer );
                    break;
                case 1:
                case 2:
                case 3:
                    return ResponseEntity.unprocessableEntity().build();
                case 4:
                    return ResponseEntity.status( 450 ).build();
            }
        }catch( InterruptedException e ){
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.TOO_MANY_REQUESTS ).build();
        }catch( IOException e ){
            return ResponseEntity.unprocessableEntity().build();
        }
        return ResponseEntity.ok().build();
    }

    // Проверено
    @RequestMapping( value = "/doneSurvey", method = RequestMethod.DELETE )
    public ResponseEntity<?> deleteUserAnswer( @RequestParam String login, @RequestParam Integer id ){
        try{
            userService.deleteUserAnswer( id, login );
            return ResponseEntity.ok().build();
        }catch( IOException e ){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }catch( InterruptedException e ){
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.TOO_MANY_REQUESTS ).build();
        }
    }

    //   Проверено
    @RequestMapping( value = "/createdSurvey", method = RequestMethod.POST )
    public ResponseEntity<?> newSurvey( @RequestParam String createdSurvey ){
        try{
            // TODO: 21.06.17 Валидация
            return ResponseEntity.ok(
                    surveyService.saveSurvey( objectMapper.readValue( createdSurvey, Survey.class ) ).get() );
        }catch( InterruptedException e ){
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.TOO_MANY_REQUESTS ).build();
        }catch( IOException e ){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    //   Проверено
    @RequestMapping( value = "/img", method = RequestMethod.GET )
    public ResponseEntity<?> getImage( @RequestParam String id ) throws IOException{
        MyFile file = filesManager.getFile( id + ".png" );
        return ResponseEntity.accepted().headers( file.getHeaders() ).body( file.getBytes() );
    }

    //   Проверено
    @RequestMapping( value = "/img/upload", method = RequestMethod.POST )
    public ResponseEntity<?> uploadPic( @RequestParam( value = "profile_picture" ) MultipartFile file,
                                        @RequestParam( value = "userLogin" ) String userLogin ) throws IOException{
        String type = new LinkedList<>(
                Arrays.asList( file.getOriginalFilename().split( "\\." ) ) ).getLast().toLowerCase();
        if( !( type.equals( "jpg" ) || type.equals( "png" ) ) )
            ResponseEntity.badRequest().contentType( MediaType.APPLICATION_JSON_UTF8 ).body(
                    messageSource.getMessage( "File.notJPG", new String[]{}, Locale.getDefault() ) );
        filesManager.saveFile( file.getBytes(), userLogin );
        return ResponseEntity.ok().build();
    }

    //   Проверено
    @RequestMapping( value = "/img/delete", method = RequestMethod.DELETE )
    public ResponseEntity<?> deletePic( @RequestParam( value = "login" ) String location ) throws IOException{
        filesManager.deleteFile( location );
        return ResponseEntity.ok().build();
    }
}

