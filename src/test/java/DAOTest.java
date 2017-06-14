import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import ru.ssau.config.WebAppConfig;
import ru.ssau.domain.Category;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;

import java.util.Date;
import java.util.concurrent.Semaphore;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = WebAppConfig.class )
@WebAppConfiguration
public class DAOTest{

    private Survey survey;

    @Autowired
    private Semaphore  semaphore;

    {
        survey = new Survey();
        survey.setName( "test" );
        survey.setComment( "test" );
        User user = new User();
        user.setLogin( "login" );
        survey.setCreator( user );
        survey.setDate( new Date() );
        Category category = new Category();
        category.setName( "category" );
        survey.setCategory( category );
    }


}
