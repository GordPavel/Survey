import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import ru.ssau.DAO.survey.DeserializeCategoryOptions;
import ru.ssau.DAO.survey.DeserializeSurveyOptions;
import ru.ssau.DAO.survey.SurveyDAO;
import ru.ssau.DAO.survey.SurveysSort;
import ru.ssau.config.WebAppConfig;
import ru.ssau.domain.Category;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;

import java.util.Date;
import java.util.List;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = WebAppConfig.class )
@WebAppConfiguration
public class DAOTest{

    private Survey    survey;
    @Autowired
    private SurveyDAO surveyDAO;

    {
        survey = new Survey( 1 );
        User user = new User();
        user.setLogin( "login" );
        survey.setCreator( user );
        survey.setDate( new Date() );
        Category category = new Category();
        category.setName( "category2" );
        survey.setCategory( category );
    }

    @Test
    public void saveSurvey() throws Exception{
        for( int i = 0 ; i < 10 ; i++ ){
            survey.getCategory().setName( "category" + i );
            surveyDAO.saveNewSurvey( survey );
        }
    }

    @Test
    public void listAllSurveys() throws Exception{
        List<Survey> surveys = surveyDAO.listSurveysByPredicate( path -> true, SurveysSort.TIME, Integer.MAX_VALUE,
                                                                 DeserializeSurveyOptions.QUESTIONS,
                                                                 DeserializeSurveyOptions.CATEGORY );
        surveys = null;
    }

    @Test
    public void getSurvey() throws Exception{
        Survey getSurvey = surveyDAO.findSurvey( 10, DeserializeSurveyOptions.CATEGORY,
                                                 DeserializeSurveyOptions.QUESTIONS ).get();
        getSurvey = null;
    }

    @Test
    public void updateSurvey() throws Exception{
        surveyDAO.updateSurvey( 2, bdSurvey -> bdSurvey.setName( "newName" ) );
    }

    @Test
    public void deleteSurvey() throws Exception{
        surveyDAO.deleteSurvey( 1 );
    }

    @Test
    public void listAllCategories() throws Exception{
        surveyDAO.listCategories( DeserializeCategoryOptions.SURVEYS ).forEach( category ->{
            System.out.println( category.getName() );
            category.getSurveys().forEach( survey1 -> System.out.println( " " + survey1.getId() ) );
        } );
    }
}
