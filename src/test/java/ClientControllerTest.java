import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.ssau.config.WebAppConfig;
import ru.ssau.domain.User;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = WebAppConfig.class )
@WebAppConfiguration
public class ClientControllerTest{

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private String filesDir = "/surveyProjectFiles/";

    @Before
    public void setMockMvc(){
        mockMvc = MockMvcBuilders.webAppContextSetup( webApplicationContext ).build();
    }

    @Test
    public void getUser() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.readValue(
                mockMvc.perform( get( "/client/userLogin=admin" ) ).andReturn().getResponse().getContentAsString(),
                User.class );
    }

    @Test
    public void newUserWithFile() throws Exception{
        mockMvc.perform(
                fileUpload( "/client/registration" )
                        .file( new MockMultipartFile( "file",
                                                      Files.newInputStream( Paths.get( filesDir + "1.jpeg" ) ) ) )
                        .param( "login", "anon111" ).param( "password", "1234567890" )
        ).andReturn();
        Assert.assertTrue( Files.exists( Paths.get( filesDir + "anon111.jpeg" ) ) );
        Files.delete( Paths.get( filesDir + "anon111.jpeg" ) );
    }

    @Test
    public void getImage() throws Exception{
        Assert.assertEquals( Files.size( Paths.get( filesDir + "1.jpeg" ) ),

                             mockMvc.perform( get( "/client/img=1" ) )

                                     .andExpect( content().contentType( MediaType.IMAGE_JPEG ) )

                                     .andExpect( status().isCreated() )

                                     .andReturn().getResponse().getContentLength() );

        mockMvc.perform( get( "/client/img=0" ) ).andExpect( status().isNotFound() );

        Assert.assertEquals( Files.size( Paths.get( filesDir + "logo.png" ) ),

                             mockMvc.perform( get( "/client/img=logo" ) )

                                     .andExpect( content().contentType( MediaType.IMAGE_PNG ) )

                                     .andExpect( status().isCreated() )

                                     .andReturn().getResponse().getContentLength() );
    }
}

