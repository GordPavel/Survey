import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.ssau.config.WebAppConfig;
import ru.ssau.service.SurveyService;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = WebAppConfig.class )
@WebAppConfiguration
public class ClientControllerTest{

    @Autowired
    private WebApplicationContext webApplicationContext;


    private MockMvc mockMvc;


    private String filesDir = "/surveyProjectFiles/";
    @Autowired
    private SurveyService surveyService;

    @Before
    public void setMockMvc(){
        mockMvc = MockMvcBuilders.webAppContextSetup( webApplicationContext ).build();
    }

//    @Test
//    public void showAllSurveys(){
//        surveyService.getTop().stream().map( Survey::getName ).forEach( System.out::println );
//    }

    @Test
    public void getSurveyById(){
        System.out.println( surveyService.getSurveyById( 1 ).get() );
    }

}

