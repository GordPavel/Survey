package ru.ssau.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
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
import ru.ssau.service.SurveyService;
import ru.ssau.service.UserService;
import ru.ssau.service.filesmanager.FilesManager;
import ru.ssau.service.filesmanager.MyFile;
import ru.ssau.service.validation.NewUserAnswerValidator;
import ru.ssau.service.validation.UserRegistrationValidator;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private final Environment               environment;


    @Autowired
    public ClientController( SurveyService surveyService, UserService userService, FilesManager filesManager,
                             UserRegistrationValidator validator, ShaPasswordEncoder passwordEncoder,
                             ObjectMapper objectMapper, @Qualifier( "messageSource" ) MessageSource messageSource,
                             NewUserAnswerValidator userAnswerValidator, Environment environment ){
        this.surveyService = surveyService;
        this.userService = userService;
        this.filesManager = filesManager;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
        this.userAnswerValidator = userAnswerValidator;
        this.environment = environment;
    }


    /***
     *
     * @param sortBy how to sort all surveys
     *               @see SurveysSort, default = by time to faster loading
     * @param limit how many surveys download , default = 5
     *
     * @param options survey load options
     *                @see DeserializeSurveyOptions, you can control only download users answers or not to sort by user
     *
     *  @return List of downloaded surveys
     */
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

    /***
     *
     * @param id id to find survey
     * @param options survey load options
     *                @see DeserializeSurveyOptions
     * @return status 200 with loaded survey or status 429 if too many connections to database or 400 if incorrect options
     */
    @RequestMapping( value = "/survey", method = RequestMethod.GET )
    public ResponseEntity<?> getSurveyById( @RequestParam Integer id,
                                            @RequestParam( required = false, defaultValue = "[]" ) String options ){
        try{
            if( id == null ) return ResponseEntity.status( HttpStatus.BAD_REQUEST ).build();
            return surveyService.getSurveyById( id, objectMapper.readValue( options,
                                                                            DeserializeSurveyOptions[].class ) )
                    .map( survey -> {
                        try{
                            return ResponseEntity.ok().contentType( MediaType.APPLICATION_JSON_UTF8 )
                                    .body( objectMapper.writeValueAsString( survey ) );
                        }catch( JsonProcessingException e ){
                            e.printStackTrace();
                            return ResponseEntity.unprocessableEntity().build();
                        }
                    } )
                    .orElseGet( () -> ResponseEntity.notFound().build() );
        }catch( InterruptedException e ){
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.TOO_MANY_REQUESTS ).build();
        }catch( IOException e ){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /***
     *
     * @param login login to download User
     * @param options user load options
     *                @see DeserializeUserOptions
     * @return  status 200 with loaded user or status 429 if too many connections to database or 400 if incorrect options
     */
    @RequestMapping( value = "/user", method = RequestMethod.GET )
    public ResponseEntity<?> getUserByLogin( @RequestParam String login,
                                             @RequestParam( required = false, defaultValue = "[]" ) String options ){
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

    /***
     *
     * @param login login to find user
     * @return status 200 means that user was deleted or status 429 if too many connections to database
     */
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

    /***
     *
     * @param login to find user and download al his done surveys
     * @return List of done surveys of user
     */
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


    /***
     *
     * @param login to find user and download al his made surveys
     * @return List of made surveys of user
     */
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

    /***
     *
     * @param login to find user and download al his made surveys
     * @param newName new name of user to change
     * @return
     */
    @RequestMapping( value = "/user/changeName" , method = RequestMethod.POST )
    public ResponseEntity<?> changeUserName( @RequestParam String login , @RequestParam String newName ){
        userService.changeUserName( login , newName );
        return ResponseEntity.ok().build();
    }

    /***
     *
     * @param login to find user and download al his made surveys
     * @param newSurname new surname of user to change
     * @return
     */
    @RequestMapping( value = "/user/changeSurname" , method = RequestMethod.POST )
    public ResponseEntity<?> changeUseLastName( @RequestParam String login , @RequestParam String newSurname ){
        userService.changeUserLastName( login , newSurname );
        return ResponseEntity.ok().build();
    }

    /***
     *
     * @param login login to authenticate on server
     * @param password password to authenticate on server
     * @param options user load options
     *                @see DeserializeUserOptions
     * @return status 200 and loaded user if all is correct , 404 if incorrect login , 400 if incorrect password or incorrect options ,
     * 429 if too many connections to database
     */
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

    /***
     *
     * @param JSONUser JSON string of new user entity to save on database
     * @return 200 if new user is correct , 420 if login is contains in database, 422 too short or too long login , 421 too short or too long password
     * 429 if too many connections to database , 400 if bad json string
     */
    @RequestMapping( value = "/registration", method = RequestMethod.POST )
    public ResponseEntity<?> newUser( @RequestParam( "newUser" ) String JSONUser ){
        try{
            User user = objectMapper.readValue( JSONUser, User.class );
            int  status;
            if( ( status = validator.validate( user ) ) == 0 ){
                userService.saveUser( user );
                return ResponseEntity.ok().build();
            }else return ResponseEntity.status( status ).build();
        }catch( InterruptedException e ){
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.TOO_MANY_REQUESTS ).build();
        }catch( IOException e ){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /***
     *
     * @param downloadSurveys if you want too download all surveys, contains in topic
     * @param sortBy type of sorting of all topics and surveys in each topic
     * @param limit max  size of List of surveys in topic
     * @return  status 200 and List of topics with list List of surveys or 429 if too many connections to database
     */
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

    /***
     *
     * @param answers List of Integer with numbers of answers in each question
     * @param id id of done survey
     * @param login login of user who dine this survey
     * @return status 200 - alright , status 422 if bad params, 450 if this user already done this survey
     */
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

    /***
     *
     * @param login wants to delete his done of survey
     * @param id login wants to delete his daone of id survey
     * @return
     */
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

    /***
     *
     * @param createdSurvey JSON string of new survey entity to save on database
     * @return 200 if new survey is correct , 420 if login is contains in database, 422 too short or too long login , 421 too short or too long password
     */
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


    /***
     *
     * @param message to log in system of Android clientbugs
     */
    @RequestMapping( value = "/exception" , method = RequestMethod.POST )
    public void addErrors( @RequestParam String message ){
        try{
            String name = DateTimeFormatter.ofPattern( "dd.MM.yyyy" ).format( LocalDate.now() );
            Boolean addOrCreate = Files.list( getExceptionsDirectory() ).anyMatch( path -> path.toString().substring( getExceptionsDirectoryNameLength() ).equals( name ) );
            StandardOpenOption option = addOrCreate ? StandardOpenOption.APPEND : StandardOpenOption.CREATE_NEW;
            String add = addOrCreate ? "\n" : "";
            Files.write( Paths.get( getExceptionsDirectory() + "/" + name ) ,
                         ( message + "\n" + add ).getBytes( Charset.forName( "utf-8" ) ) , option );
        }catch( IOException e ){
            e.printStackTrace();
        }
    }

    /***
     *
     * @param id user's login who owns this pic
     * @return bytes stream of pic and content type
     * @throws IOException bad access to file
     */
    @RequestMapping( value = "/img", method = RequestMethod.GET )
    public ResponseEntity<?> getImage( @RequestParam String id ) throws IOException{
        MyFile file = filesManager.getFile( id + ".png" );
        return ResponseEntity.accepted().headers( file.getHeaders() ).body( file.getBytes() );
    }

    /***
     *
     * @param file Multipart file of uploading pic
     * @param userLogin login of user, who downloads pic
     * @return status 200 if alright or 400 of file not jpg or png or if can't read butes stream
     * @throws IOException
     */
    @RequestMapping( value = "/img/upload", method = RequestMethod.POST )
    public ResponseEntity<?> uploadPic( @RequestParam( value = "profile_picture" ) MultipartFile file,
                                        @RequestParam( value = "userLogin" ) String userLogin ){
        String type = new LinkedList<>(
                Arrays.asList( file.getOriginalFilename().split( "\\." ) ) ).getLast().toLowerCase();
        if( !( type.equals( "jpg" ) || type.equals( "png" ) ) )
            ResponseEntity.badRequest().contentType( MediaType.APPLICATION_JSON_UTF8 ).body(
                    messageSource.getMessage( "File.notJPG", new String[]{}, Locale.getDefault() ) );
        try{
            filesManager.saveFile( file.getBytes(), userLogin );
        }catch( IOException e ){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /***
     *
     * @param location login of user who wants to delete his profile pic
     * @return status 200 if alright or status 400 if could
     */
    @RequestMapping( value = "/img/delete", method = RequestMethod.DELETE )
    public ResponseEntity<?> deletePic( @RequestParam( value = "login" ) String location ){
        try{
            filesManager.deleteFile( location );
        }catch( IOException e ){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    private Path getExceptionsDirectory(){
        return Paths.get( environment.getProperty( "storage" ) + environment.getProperty( "exceptions" ) );
    }
    private Integer getExceptionsDirectoryNameLength(){
        return getExceptionsDirectory().toString().length() + 1;
    }
}

