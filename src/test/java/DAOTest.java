import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.ssau.DAO.enums.DeserializeSurveyOptions;
import ru.ssau.DAO.enums.SurveysSort;
import ru.ssau.config.WebAppConfig;
import ru.ssau.domain.Category;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;
import ru.ssau.domain.UserRoles;
import ru.ssau.service.SurveyService;
import ru.ssau.service.UserService;
import ru.ssau.service.validation.NewUserAnswerValidator;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = WebAppConfig.class )
@WebAppConfiguration
public class DAOTest{

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    private User     user;
    private Category category;
    private Survey   survey;

    @Autowired
    private SurveyService          surveyService;
    @Autowired
    private NewUserAnswerValidator userAnswerValidator;
    @Autowired
    private UserService            userService;

    {
        user = new User();
        user.setLogin( "login4" );
        user.setName( "name" );
        user.setLastName( "lastName" );
        user.setPassword( "password" );
        user.setRole( UserRoles.USER );

        category = new Category();
        category.setName( "category" );

        survey = new Survey( 3 );
        survey.setCreator( user );
        survey.setCategory( category );
    }

    @Before
    public void setUp() throws Exception{
        mockMvc = MockMvcBuilders.webAppContextSetup( webApplicationContext ).build();
    }

    @Test
    public void saveUser() throws Exception{
        System.out.println( mockMvc.perform( post( "/client/registration" )
                                                     .param( "newUser", objectMapper.writeValueAsString( user ) ) )
                                    .andReturn().getResponse().getStatus() );
    }

    @Test
    public void saveSurveys() throws Exception{
        Survey survey = new Survey( 3 );
        user.setLogin( "top" );
        survey.setCreator( user );
        survey.setCategory( category );
        surveyService.saveSurvey( survey );
    }

    @Test
    public void createSurvey() throws Exception{
        Survey survey = new Survey( 3 );
        user.setLogin( "top" );
        survey.setCreator( user );
        survey.setCategory( category );
        String createdSurvey = objectMapper.writeValueAsString( survey );
        mockMvc.perform(
                post( "/client/createdSurvey" ).contentType( MediaType.APPLICATION_JSON_UTF8 ).param( "createdSurvey",
                                                                                                      createdSurvey ) );
    }

    @Test
    public void getSurveyByController() throws Exception{
        MvcResult result = mockMvc.perform(
                get( "/client/survey" ).param( "id", String.valueOf( 50 ) ).param( "options",
                                                                                   objectMapper.writeValueAsString(
                                                                                           new DeserializeSurveyOptions[]{
                                                                                                   DeserializeSurveyOptions.CREATOR ,
                                                                                                   DeserializeSurveyOptions.QUESTIONS ,
                                                                                                   DeserializeSurveyOptions.CATEGORY ,
                                                                                                   DeserializeSurveyOptions.STATISTICS } ) ) ).andReturn();
        Survey survey = objectMapper.readValue( result.getResponse().getContentAsString(), Survey.class );
        survey.getAnswers().forEach( userAnswer -> {
            System.out.println( userAnswer.getUser().getLogin() + " -> " + userAnswer.getSurvey().getId() );
            userAnswer.getAnswers().forEach( System.out::println );
        } );
    }

    @Test
    public void saveUserAnswer() throws Exception{
        String login   = userService.getUser( "pvgordeev" ).get().getLogin();
        String id      = surveyService.getSurveyById( 51 ).get().getId().toString();
        String answers = objectMapper.writeValueAsString( Arrays.asList( 1, 2, 0, 3, 1, 3, 4, 0, 2, 0 ) );
        mockMvc.perform( post( "/client/doneSurvey" ).contentType( MediaType.APPLICATION_JSON_UTF8 ).param( "login",
                                                                                                            login ).param(
                "id", id ).param( "answers", answers ) );
    }

    @Test
    public void getSurvey() throws Exception{
        Survey survey = surveyService.getSurveyById( 50, DeserializeSurveyOptions.CREATOR,
                                                     DeserializeSurveyOptions.QUESTIONS,
                                                     DeserializeSurveyOptions.CATEGORY,
                                                     DeserializeSurveyOptions.STATISTICS ).get();
        System.out.println( survey.getId() + " " + survey.getName() );
        System.out.println( survey.getCreator().getLogin() );
        survey.getQuestions().forEach( question -> {
            System.out.println( "   " + question.getId() + " " + question.getName() );
            question.getAnswers().forEach( answer -> System.out.println(
                    "      " + answer.getId() + " " + answer.getName() + " " + answer.getUsersAnswered() ) );
        } );
    }

    @Test
    public void getMadeUser() throws Exception{
        System.out.println( surveyService.getMadeUser( 2 ).get().getLogin() );
    }

    @Test
    public void getCategories() throws Exception{
        surveyService.getCategories( true, SurveysSort.TIME, 7 ).forEach( category1 -> {
            System.out.println( category1.getName() );
            category1.getSurveys().forEach(
                    survey -> System.out.println( "     " + survey.getId() + " " + survey.getName() ) );
        } );
    }

    @Test
    public void getMadeSurveysByLogin() throws Exception{
        userService.getMadeSurveysByLogin( "user" ).forEach(
                survey -> System.out.println( survey.getId() + " " + survey.getName() ) );
    }

    @Test
    public void deleteUserAnswer() throws Exception{
        userService.deleteUserAnswer( 4, "user" );
    }

    @Test
    public void deleteSurvey() throws Exception{
        surveyService.deleteSurvey( 3 );
    }

    @Test
    public void deleteUser() throws Exception{
        mockMvc.perform( delete( "/client/user" ).param( "login", "s3rius" ) );
    }

    @Test
    public void addError() throws Exception{
        mockMvc.perform( post( "/client/exception" ).param( "message" , "exception" ) );
    }
}
