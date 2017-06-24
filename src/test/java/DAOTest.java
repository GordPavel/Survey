import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.ssau.config.WebAppConfig;
import ru.ssau.domain.Category;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;
import ru.ssau.domain.UserRoles;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = WebAppConfig.class )
@WebAppConfiguration
public class DAOTest{

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, User>     users      = new HashMap<>();
    private Map<String, Category> categoties = new HashMap<>();
    private Map<String, Survey>   surveys    = new HashMap<>();
    @Autowired
    private ShaPasswordEncoder passwordEncoder;

    {
        users.put( "login1" , new User( "login1" , "password" , "name" , "lasrName" , UserRoles.USER ) );
        users.put( "shortLogin" , new User( "log" , "password" , "name" , "lasrName" , UserRoles.USER ) );
        users.put( "shortPassword" , new User( "login4" , "passw" , "name" , "lasrName" , UserRoles.USER ) );
    }

    @Before
    public void setUp() throws Exception{
        mockMvc = MockMvcBuilders.webAppContextSetup( webApplicationContext ).build();
    }

    @Test
    public void registration() throws Exception{
        Assert.assertEquals(200 ,  mockMvc.perform( post( "/client/registration" )
                                                      .param( "newUser",
                                                              objectMapper.writeValueAsString( users.get( "login1" ) ) ) )
                                     .andReturn().getResponse().getStatus() );
        User user = objectMapper.readValue( mockMvc.perform( get( "/client/user" ).param( "login" , "login1" ) ).andReturn()
             .getResponse().getContentAsString() , User.class );
        users.get( "login1" ).setPassword( passwordEncoder.encodePassword( users.get( "login1" ).getPassword() , null ) );
        Assert.assertEquals( user , users.get( "login1" ) );
        Assert.assertEquals( 420 , mockMvc.perform( post( "/client/registration" )
                                                      .param( "newUser",
                                                              objectMapper.writeValueAsString( users.get( "login1" ) ) ) )
                                     .andReturn().getResponse().getStatus() );
        Assert.assertEquals( 422 ,  mockMvc.perform( post( "/client/registration" )
                                                      .param( "newUser",
                                                              objectMapper.writeValueAsString( users.get( "shortLogin" ) ) ) )
                                     .andReturn().getResponse().getStatus() );
        Assert.assertEquals( 421 , mockMvc.perform( post( "/client/registration" )
                                                      .param( "newUser",
                                                              objectMapper.writeValueAsString( users.get( "shortPassword" ) ) ) )
                                     .andReturn().getResponse().getStatus() );
        mockMvc.perform( delete( "/client/user" ).param( "login" , "login1" ) );
        Assert.assertEquals( HttpStatus.NOT_FOUND.value() , mockMvc.perform( get( "/client/user" ).param( "login" , "login1" ) )
                .andReturn().getResponse().getStatus() );
    }

    @Test
    public void manyUsers() throws Exception{
        for( int i = 0 ; i < 20 ; i++ ){
            mockMvc.perform( post( "/client/registration" ).param( "newUser" ,
                                                                   objectMapper.writeValueAsString( new User( "login" + i , "password" , "name" ,
                                                                                                              "lastName" , UserRoles.USER ) ) ) );
        }
    }

    @Test
    public void manySurveys() throws Exception{
        User user;
        Category category;
        Survey survey;
        for( int i = 0 ; i < 60 ; i++ ){
            user = objectMapper.readValue( mockMvc.perform( get( "/client/user" ).param( "login" , "login" + ( i % 20 ) ) ).andReturn()
                                                   .getResponse().getContentAsString() , User.class );
            List<Category> categories = objectMapper.readValue( mockMvc.perform( get( "/client/topics" ) ).andReturn().getResponse().getContentAsString() , new TypeReference<List<Category>>(){} );
            categories.sort( Comparator.comparing( Category::getName ) );
            category = categories.get( i % 4 );
            survey = new Survey( i , "name" , "test" , user , category );
            mockMvc.perform( post( "/client/createdSurvey" ).param( "createdSurvey" , objectMapper.writeValueAsString( survey ) ) );
        }
    }

    @Test
    public void deleteSurvey() throws Exception{
        String deletingLogin = "login7";
        final List<Integer> indexesOfDeletedSurveys = Files.list( Paths.get( "/survey/survey" ) )
                .filter( path -> !path.endsWith( ".DS_Store" ) )
                .filter( path -> {
                    String str = path.toString().substring( "/survey/survey".length() + 1 );
                    return str.substring( str.indexOf( "_" ) + 1 , str.lastIndexOf( "_" ) ).equals( deletingLogin );
                } )
                .map( path -> {
                    String str = path.toString().substring( "/survey/survey".length() + 1 );
                    return Integer.parseInt( str.substring( 0 , str.indexOf( "_" ) ) );
                } )
                .collect( Collectors.toList() );
        mockMvc.perform( delete( "/client/user" ).param( "login" , deletingLogin ) );
        Assert.assertTrue( Files.list( Paths.get( "/survey/user" ) ).noneMatch( path -> path.toString().substring( "/survey/user".length() + 1 ).equals( "login5" ) ) );
        Assert.assertTrue( Files.list( Paths.get( "/survey/survey" ) ).filter( path -> !path.endsWith( ".DS_Store" ) ).noneMatch( path -> {
            String str = path.toString().substring( "/survey/survey".length() + 1 );
            return str.substring( str.indexOf( "_" ) + 1 , str.lastIndexOf( "_" ) ).equals( deletingLogin );
        } ) );
        Assert.assertTrue( Files.list( Paths.get( "/survey/question" ) ).filter( path -> !path.endsWith( ".DS_Store" ) ).noneMatch( path -> {
            String str = path.toString().substring( "/survey/question".length() + 1 );
            return indexesOfDeletedSurveys.contains( Integer.parseInt( str.substring( str.indexOf( "_" ) + 1 ) ) );
        } ) );
        Assert.assertTrue( Files.list( Paths.get( "/survey/answer" ) ).filter( path -> !path.endsWith( ".DS_Store" ) ).noneMatch( path -> {
            String str = path.toString().substring( "/survey/answer".length() + 1 );
            return indexesOfDeletedSurveys.contains( Integer.parseInt( str.substring( str.lastIndexOf( "_" ) + 1 ) ) );
        } ) );
        Assert.assertTrue( Files.list( Paths.get( "/survey/userAnswer" ) ).filter( path -> !path.endsWith( ".DS_Store" ) )
                                   .noneMatch( path -> {
            String nameFile = path.toString().substring( "/survey/userAnswer".length() + 1 );
            String login = nameFile.substring( 0 , nameFile.indexOf( "_" ) );
            Integer id = Integer.parseInt( nameFile.substring( nameFile.indexOf( "_" ) + 1 ) );
            return login.equals( deletingLogin ) || indexesOfDeletedSurveys.contains( id );
        } ) );
    }

    @Test
    public void addError() throws Exception{
        mockMvc.perform( post( "/client/exception" ).param( "message", "exception" ) );
    }
}
