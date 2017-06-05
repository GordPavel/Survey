import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.ssau.DAO.TopicRepository;
import ru.ssau.DAO.UserAnswersRepository;
import ru.ssau.config.WebAppConfig;
import ru.ssau.domain.*;
import ru.ssau.service.SurveyService;
import ru.ssau.service.UserService;

import java.util.Arrays;
import java.util.Date;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = WebAppConfig.class )
@WebAppConfiguration
public class ClientControllerTest{

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private SurveyService   surveyService;
    @Autowired
    private UserService     userService;
    @Qualifier( "topicRepository" )
    @Autowired
    private TopicRepository topicRepository;

    private String filesDir = "/surveyProjectFiles/";
    @Qualifier( "userAnswersRepository" )
    @Autowired
    private UserAnswersRepository userAnswersRepository;

    @Before
    public void setMockMvc(){
        mockMvc = MockMvcBuilders.webAppContextSetup( webApplicationContext ).build();
    }

    @Test
    public void getUserByLogin() throws Exception{
        User user = userService.getUser( "user" ).get();
        user = null;
    }

    @Test
    public void addNewAnswer() throws Exception{
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setSurvey( surveyService.getSurveyById( 2 ).get() );
        userAnswer.setUser( userService.getUser( "admin" ).get() );
        userAnswer.setUserAnswer( "[1,2]" );
        userService.addAnswer( userAnswer );
    }

    @Test
    public void getSurveys(){
        surveyService.getTop( "time", 5 ).forEach( survey -> System.out.println(
                survey.getName() + " " + survey.getUsersAnswered() + " " + survey.getDate() ) );
    }

    @Test
    public void getSurveyById() throws Exception{
        Survey survey = surveyService.getSurveyById( 1 ).get();
        System.out.println( survey.getName() + " " + survey.getComment() );
        survey.getQuestions().forEach( question -> {
            System.out.println( "   " + question.getName() );
            question.getAnswers().forEach( answer -> System.out.println( "      " + answer.getName() ) );
        } );
    }

    @Test
    public void addNewSurvey() throws Exception{
        Survey survey = new Survey();
        survey.setName( "test4" );
        survey.setComment( "test4" );
        survey.setDate( new Date() );
        survey.setTopic( topicRepository.findOne( "category1" ) );
        survey.setCreator( userService.getUser( "admin" ).get() );

        Question question1 = new Question();
        question1.setName( "question1" );
        question1.setSurvey( survey );

        Answer answer11 = new Answer();
        answer11.setName( "answer1" );
        answer11.setQuestion( question1 );

        Answer answer12 = new Answer();
        answer12.setName( "answer2" );
        answer12.setQuestion( question1 );

        question1.setAnswers( Arrays.asList( answer11, answer12 ) );

        Question question2 = new Question();
        question2.setName( "question2" );
        question2.setSurvey( survey );

        Answer answer21 = new Answer();
        answer21.setName( "answer1" );
        answer21.setQuestion( question2 );

        Answer answer22 = new Answer();
        answer22.setName( "answer2" );
        answer22.setQuestion( question2 );

        question2.setAnswers( Arrays.asList( answer21, answer22 ) );

        survey.setQuestions( Arrays.asList( question1, question2 ) );

        surveyService.saveSurvey( survey );
    }

    @Test
    public void updateSurvey() throws Exception{
        Survey survey = surveyService.getSurveyById( 1 ).get();
        survey.setComment( "обновление1" );
        surveyService.saveSurvey( survey );
    }

    @Test
    public void deleteSurvey() throws Exception{
        surveyService.deleteSurvey( surveyService.getSurveyById( 7 ).get() );
    }
}

