package ru.ssau.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

public class Initializer implements WebApplicationInitializer{

    private static final String DISPATCHER_SERVLET_NAME = "dispatcher";

    @Override
    public void onStartup( ServletContext servletContext ) throws ServletException{
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register( WebAppConfig.class );
        ctx.register( SecurityConfig.class );
        servletContext.addListener( new ContextLoaderListener( ctx ) );

        ctx.setServletContext( servletContext );

        servletContext.addFilter( "securityFilter",
                                  new DelegatingFilterProxy( "springSecurityFilterChain" ) ).addMappingForUrlPatterns(
                null, false, "/*" );

        Dynamic servlet = servletContext.addServlet( DISPATCHER_SERVLET_NAME, new DispatcherServlet( ctx ) );
        servlet.addMapping( "/" );
        servlet.setLoadOnStartup( 1 );
    }
}
