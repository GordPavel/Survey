//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import ru.ssau.DAO.enums.DeserializeSurveyOptions;
//import ru.ssau.DAO.enums.SurveysSort;
//import ru.ssau.config.WebAppConfig;
//import ru.ssau.domain.*;
//import ru.ssau.service.SurveyService;
//import ru.ssau.service.UserService;
//import ru.ssau.service.validation.NewUserAnswerValidator;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//@RunWith( SpringJUnit4ClassRunner.class )
//@ContextConfiguration( classes = WebAppConfig.class )
//@WebAppConfiguration
//public class DAOTest{
//
//    private User          user;
//    private Category      category;
//    @Autowired
//    private SurveyService surveyService;
//    @Autowired
//    private UserService   userService;
//
//    List<Survey> surveys;
//    @Autowired
//    private NewUserAnswerValidator userAnswerValidator;
//
//    {
//        user = new User();
//        user.setLogin( "user" );
//        user.setName( "name" );
//        user.setLastName( "lastName" );
//        user.setPassword( "password" );
//        user.setRole( UserRoles.USER );
//
//        category = new Category();
//        category.setName( "category" );
//
//        surveys = new ArrayList<>();
//        for( int i = 0 ; i < 50 ; i++ ){
//            Survey survey = new Survey( i );
//            survey.setCreator( user );
//            survey.setCategory( category );
//            surveys.add( survey );
//        }
//    }
//
//    @Test
//    public void saveUser() throws Exception{
//        userService.saveUser( user );
//    }
//
//    @Test
//    public void saveSurveys() throws Exception{
//        surveys.forEach( survey -> {
//            try{
//                Category category = survey.getCategory();
//                category.setName( "category" + ( int ) ( Math.random() * 10 ) );
//                surveyService.saveSurvey( survey );
//            }catch( InterruptedException e ){
//                e.printStackTrace();
//            }
//        } );
//    }
//
//    @Test
//    public void saveUserAnswer() throws Exception{
//        UserAnswer userAnswer = new UserAnswer();
//        userAnswer.setUser( userService.getUser( "user1" ).get() );
//        userAnswer.setSurvey( surveyService.getSurveyById( 2 ).get() );
//        userAnswer.setAnswers( Arrays.asList( 0 , 0 , 0, 0 , 0 , 0 , 1) );
//        System.out.println( userAnswerValidator.validate( userAnswer ) );
//        userService.saveNewUserAnswer( userAnswer );
//    }
//
//    @Test
//    public void getSurvey() throws Exception{
//        Survey survey = surveyService.getSurveyById( 2 , DeserializeSurveyOptions.CREATOR , DeserializeSurveyOptions.QUESTIONS ,
//                                                     DeserializeSurveyOptions.CATEGORY , DeserializeSurveyOptions.STATISTICS ).get();
//        System.out.println( survey.getId() + " " + survey.getName() );
//        System.out.println( survey.getCreator().getLogin() );
//        survey.getQuestions().forEach( question -> {
//            System.out.println( "   " + question.getId() +  " " + question.getName() );
//            question.getAnswers().forEach( answer -> System.out.println( "      " + answer.getId() + answer.getName() + " " +
//                                                                         answer.getUsersAnswered() ) );
//        } );
//    }
//
//    @Test
//    public void getMadeUser() throws Exception{
//        System.out.println( surveyService.getMadeUser( 2 ).get().getLogin() );
//    }
//
//    @Test
//    public void getCategories() throws Exception{
//        surveyService.getCategories( true , SurveysSort.TIME , 7 ).forEach( category1 -> {
//            System.out.println( category1.getName() );
//            category1.getSurveys().forEach( survey -> System.out.println( "     " + survey.getId() + " " + survey.getName() ) );
//        } );
//    }
//
//    @Test
//    public void getMadeSurveysByLogin() throws Exception{
//        userService.getMadeSurveysByLogin( "user" ).forEach( survey ->
//            System.out.println( survey.getId() + " " + survey.getName() ) );
//    }
//
//    @Test
//    public void deleteUserAnswer() throws Exception{
//        userService.deleteUserAnswer( 4 , "user" );
//    }
//
//    @Test
//    public void deleteSurvey() throws Exception{
//        surveyService.deleteSurvey( 3 );
//    }
//
//    @Test
//    public void deleteUser() throws Exception{
//        userService.deleteUser( "user" );
//    }
//}
