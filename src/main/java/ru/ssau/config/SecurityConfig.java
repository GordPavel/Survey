package ru.ssau.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import ru.ssau.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity( securedEnabled = true )
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /***
     *
     * @param auth registrates user service adapter to spring authentications
     * @throws Exception don't know, why
     */

    @Autowired
    public void registerGlobalAuthentication( AuthenticationManagerBuilder auth ) throws Exception{
        auth.userDetailsService( userDetailsService ).passwordEncoder( getShaPasswordEncoder() );
    }

    /***
     *
     * @param http configure protected pages
     * @throws Exception don't know, why
     */
    @Override
    protected void configure( HttpSecurity http ) throws Exception{
        http

                .csrf().disable()

                .authorizeRequests()

                .antMatchers( "/user" , "/newSurvey" ).authenticated()

                .anyRequest().permitAll();

        http

                .formLogin().loginPage( "/login" )

                .loginProcessingUrl( "/j_spring_security_check" )

                .failureUrl( "/login?error" )

                .usernameParameter( "login" )

                .passwordParameter( "password" ).permitAll();

        http.logout().permitAll().logoutUrl( "/logout" ).logoutSuccessUrl( "/login?logout" ).invalidateHttpSession(
                true );
    }

    /***
     *
     * @return password encoder for user passw
     */
    @Bean
    public ShaPasswordEncoder getShaPasswordEncoder(){
        return new ShaPasswordEncoder( 256 );
    }

}