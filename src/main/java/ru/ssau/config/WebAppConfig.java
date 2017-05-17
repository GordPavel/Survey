package ru.ssau.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import ru.ssau.service.UserDetailsServiceImpl;

@Configuration
@EnableWebMvc
@ComponentScan( "ru.ssau" )
public class WebAppConfig extends WebMvcConfigurerAdapter{

    @Override
    public void addResourceHandlers( ResourceHandlerRegistry registry ){
        registry.addResourceHandler( "/pages/**" ).addResourceLocations( "/pages/" );
    }

    @Bean
    public UserDetailsService getUserDetailsService(){
        return new UserDetailsServiceImpl();
    }

    @Bean
    public InternalResourceViewResolver setupViewResolver(){
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setContentType( "text/html;charset=UTF-8" );
        resolver.setPrefix( "/pages/" );
        resolver.setSuffix( ".jsp" );
        resolver.setViewClass( JstlView.class );
        return resolver;
    }

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
}

