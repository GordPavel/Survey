package ru.ssau.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import ru.ssau.service.UserDetailsServiceImpl;

import java.util.concurrent.Semaphore;

@Configuration
@EnableWebMvc
@PropertySource( "classpath:app.properties" )
@ComponentScan( "ru.ssau" )
public class WebAppConfig extends WebMvcConfigurerAdapter{

    /***
     *
     * @param registry registry resources files
     */
    @Override
    public void addResourceHandlers( ResourceHandlerRegistry registry ){
        registry.addResourceHandler( "/pages/**" ).addResourceLocations( "/pages/" );
    }

    /***
     *
     * @return JSON objects mapper
     */
    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }


    /***
     *
     * @return My implementation of UserDetailsService
     * @see UserDetailsService
     *
     */
    @Bean
    public UserDetailsService getUserDetailsService(){
        return new UserDetailsServiceImpl();
    }


    /***
     *
     * @return jsp's resolver
     */
    @Bean
    public InternalResourceViewResolver setupViewResolver(){
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setContentType( "text/html;charset=UTF-8" );
        resolver.setPrefix( "/pages/" );
        resolver.setSuffix( ".jsp" );
        resolver.setViewClass( JstlView.class );
        return resolver;
    }


    /***
     *
     * @return MultipartFile resolver for downloading and uploading pics
     * @see org.springframework.web.multipart.MultipartFile
     */
    @Bean
    CommonsMultipartResolver multipartResolver(){
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize( 5000000 );
        return resolver;
    }

    @Bean
    public MessageSource messageSource(){
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setDefaultEncoding( "UTF-8" );
        messageSource.setBasename( "messages" );
        return messageSource;
    }


    private Semaphore semaphore = new Semaphore( 50, true );

    /***
     *
     * @return semaphore for protecting database of too many connections
     */
    @Bean
    public Semaphore semaphore(){
        return semaphore;
    }
}

