import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import ru.ssau.DAO.DAO;
import ru.ssau.DAO.enums.DeserializeSurveyOptions;
import ru.ssau.DAO.enums.DeserializeUserOptions;
import ru.ssau.DAO.enums.SurveysSort;
import ru.ssau.config.WebAppConfig;
import ru.ssau.domain.*;
import ru.ssau.exceptions.UserNotFoundException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = WebAppConfig.class )
@WebAppConfiguration
public class DAOTest{

    private Survey   survey;
    private User     user;
    private Category category;
    @Autowired
    private DAO      DAO;

    {
        user = new User();
        user.setLogin( "user" );
        user.setName( "name" );
        user.setLastName( "lastName" );
        user.setPassword( "password" );
        user.setRole( UserRoles.USER );

        category = new Category();
        category.setName( "category" );

    }

    @Test
    public void saveUser() throws Exception{
        DAO.saveUser( user );
    }

    @Test
    public void saveSurvey() throws Exception{
        Date date = new Date();
        for( int i = 0 ; i < 4 ; i++ ){
            survey = new Survey( i );
            survey.setCreator( user );
            category.setName( "category" + i );
            survey.setCategory( category );
            date.setYear( date.getYear() - i );
            survey.setDate( date );
            DAO.saveNewSurvey( survey );
        }
    }

    @Test
    public void listAllSurveys() throws Exception{
        List<Survey> surveys = DAO.listSurveysByPredicate( path -> true, SurveysSort.TIME, Integer.MAX_VALUE, false ,
                                                           DeserializeSurveyOptions.QUESTIONS,
                                                           DeserializeSurveyOptions.CATEGORY ,
                                                           DeserializeSurveyOptions.CREATOR);
        surveys = null;
    }

    @Test
    public void getSurvey() throws Exception{
        Survey getSurvey = DAO.findSurvey( 11, false ,  DeserializeSurveyOptions.CATEGORY,
                                           DeserializeSurveyOptions.QUESTIONS , DeserializeSurveyOptions.USERS ).get();
        getSurvey.getQuestions().forEach( question -> {
            System.out.println( question.getId() + " " + question.getName() );
            question.getAnswers().forEach( answer -> System.out.println(
                    "    " + answer.getId() + " " + answer.getName() + " " + answer.getUsersAnswered() ));
        } );
    }

    @Test
    public void updateSurvey() throws Exception{
        DAO.updateSurvey( 2, bdSurvey -> bdSurvey.setName( "newName" ) );
    }

    @Test
    public void deleteSurvey() throws Exception{
        DAO.deleteSurvey( 2 );
    }

    @Test
    public void listAllCategories() throws Exception{
        DAO.listCategories( true , SurveysSort.USERS, Integer.MAX_VALUE ).forEach( category ->{
            System.out.println( category.getName() );
            category.getSurveys().forEach( survey1 -> {
                System.out.println( survey1.getId() + "   " + survey1.getName() + survey1.getAnswers().size() );
                survey1.getQuestions().forEach( question -> {
                    System.out.println( "       " +  question.getId() + " " + question.getName() );
                    question.getAnswers().forEach( answer -> {
                        System.out.println( "           " + answer.getId() + " " + answer.getName() + " " + answer.getUsersAnswered() );
                    } );
                } );
            } );
        } );
    }


    @Test
    public void getUser() throws Exception{
        User user = DAO.findUser( "login" , DeserializeUserOptions.MADESURVEYS ).orElseThrow( () -> new UserNotFoundException( "login" ) );
        user = null;
    }

    @Test
    public void listAllUsers() throws Exception{
        List<User> users = DAO.listAllUsers( DeserializeUserOptions.MADESURVEYS );
        users = null;
    }

    @Test
    public void updateUser() throws Exception{
        DAO.updateUser( "login3" , bdUser -> bdUser.setRole( UserRoles.USER.toString() ) );
    }

    @Test
    public void deleteUser() throws Exception{
        DAO.deleteUser( "login" );
    }

    @Test
    public void saveUserAnswer() throws Exception{
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setUser( DAO.findUser( "login3" ).get() );
        userAnswer.setSurvey( DAO.findSurvey( 11 , false ).get() );
        userAnswer.setAnswers( Arrays.asList( 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 ) );
        DAO.saveNewUserAnswer( userAnswer );
    }

    @Test
    public void listAllUserAnswers() throws Exception{
        DAO.listAllUserAnswersByUserLogin( "login" , false )
                .forEach( userAnswer -> System.out.println( userAnswer.getUser().getLogin() + " " + userAnswer.getSurvey().getId() ) );
    }

    @Test
    public void deleteUserAnswers() throws Exception{
        DAO.deleteUserAnswers( 1 );
    }
}
