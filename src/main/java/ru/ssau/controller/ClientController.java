package ru.ssau.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
    public List<Survey> topSurveys( @RequestParam( required = false, defaultValue = "users" ) String sortBy,
                                    @RequestParam( required = false, defaultValue = "5" ) Integer limit,
                                    @RequestParam( required = false, value = "[]" ) String... deserializeOptions ){
        return surveyService.getTop( sortBy, limit, DeserializeSurveyOptions.fromStrings( deserializeOptions ) );
    }

    @RequestMapping( value = "/survey", method = RequestMethod.GET )
    public String getSurveyById(@RequestParam Integer id) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(surveyService.getSurveyById(id)
                .orElseThrow(() -> new SurveyNotFoundException(id)));
    }

    @RequestMapping( value = "/user", method = RequestMethod.GET )
    public String getUserByLogin(@RequestParam String login) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(userService.getUser(login)
                .orElseThrow(() -> new UsernameNotFoundException(login)));
    }


    @RequestMapping( value = { "/user/doneSurveys" }, method = { RequestMethod.GET } )
    public String getDoneSurveysByLogin(@RequestParam String login) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(userService.getUser(login)
                .orElseThrow(() -> new UsernameNotFoundException(login)).getDoneSurveys());
    }

    @RequestMapping( value = { "/user/madeSurveys" }, method = { RequestMethod.GET } )
    public String getMadeSurveysByLogin(@RequestParam String login) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(userService.getUser(login).get().getMadeSurveys());
    }

    @RequestMapping( value = "/login", method = RequestMethod.POST )
    public ResponseEntity<?> login( @RequestParam String login, @RequestParam String password ){
        Optional<User> userOptional = userService.getUser(login);
        if (!userOptional.isPresent())
            return ResponseEntity.notFound().build();
        User user = userOptional.get();
        if (!user.getPassword().equals(passwordEncoder.encodePassword(password, null)))
            return ResponseEntity.badRequest().build();
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON_UTF8).body(user);

    }

    @RequestMapping( value = "/registration", method = RequestMethod.POST )
    public boolean newUser(@RequestParam("newUser") String JSONUser) throws IOException {
        User user = new ObjectMapper().readValue(JSONUser, User.class);
        if (validator.validate(user)) {
            userService.saveUser(user);
            return true;
        } else
            return false;
    }

    @RequestMapping( value = "/topics", method = RequestMethod.GET )
    public ResponseEntity<?> topics( @RequestParam( required = false, defaultValue = "users" ) String sortBy,
                                     @RequestParam( required = false, defaultValue = "3" ) Integer limit ){
        List list;
        if (sortBy.equals("users")) {
            list = this.surveyService.getCategories(true, SurveysSort.USERS, 20).stream().map((category) -> {
                return new TopicTransport(category.getName(), category.getSurveys().stream().sorted(Comparator.comparingInt(Survey::getUsersDone)).limit((long) limit.intValue()).map((survey) -> {
                    return new SurveyTransport(survey.getId(), survey.getName());
                }).collect(Collectors.toList()));
            }).collect(Collectors.toList());
        } else {
            list = this.surveyService.getCategories(true, SurveysSort.USERS, 20).stream().map((category) -> {
                return new TopicTransport(category.getName(), category.getSurveys().stream().sorted(Comparator.comparingLong((survey) -> {
                    return survey.getDate().getTime();
                })).limit((long) limit.intValue()).map((survey) -> {
                    return new SurveyTransport(survey.getId(), survey.getName());
                }).collect(Collectors.toList()));
            }).collect(Collectors.toList());
        }

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON_UTF8).body(list);
    }

    @RequestMapping( value = "/topic", method = RequestMethod.GET )
    public ResponseEntity<?> getTopic( @RequestParam String name ){
        Optional<Category> optional = this.surveyService.getCategoryByName(name, true, SurveysSort.USERS, 20);
        return optional.map((category) -> {
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON_UTF8).body(new TopicTransport(category.getName(), category.getSurveys().stream().map((survey) -> {
                return new SurveyTransport(survey.getId(), survey.getName());
            }).collect(Collectors.toList())));
        }).orElseGet(() -> {
            return ResponseEntity.notFound().build();
        });
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

