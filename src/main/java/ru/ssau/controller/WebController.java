package ru.ssau.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ssau.domain.User;
import ru.ssau.service.UserService;
import ru.ssau.service.validation.UserRegistrationValidator;

import java.io.IOException;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
@RequestMapping( "/" )
public class WebController{

    @Autowired
    private UserRegistrationValidator validator;
    @Autowired
    private UserService               userService;
    @Autowired
    private MessageSource             messageSource;
    @Autowired
    private ClientController          clientController;

    @RequestMapping( method = RequestMethod.GET )
    public String start( Model model ){
        model.addAttribute( "surveys",
                            clientController.topSurveys().stream().limit( 5 ).collect( Collectors.toList() ) );
        return "index";
    }

    @RequestMapping( value = "/login", method = RequestMethod.GET )
    public String signInPage( @RequestParam( value = "error", required = false ) String error,
                              @RequestParam( value = "logout", required = false ) String logout, Model model ){
        if( error != null )
            model.addAttribute( "error", messageSource.getMessage( "login.incorrectLoginOrPassword", new String[]{},
                                                                   Locale.getDefault() ) );
        if( logout != null )
            model.addAttribute( "msg", "You've been logged out successfully." );
        return "login";
    }

    @RequestMapping( value = "/user", method = RequestMethod.GET )
    public String getUserByLogin( @RequestParam String login, Model model ){
        model.addAttribute( "user", clientController.getUserByLogin( login ) );
        // TODO: 17.05.17 Страница пользователя
        return "user";
    }

    @RequestMapping( value = "/survey", method = RequestMethod.GET )
    public String survey( @RequestParam Integer id, Model model ){
        model.addAttribute( "survey", clientController.getSurveyById( id ) );
        // TODO: 17.05.17 Страница анкеты
        return "survey";
    }

    @RequestMapping( value = "/registration", method = RequestMethod.GET )
    public String registration( Model model ){
        model.addAttribute( "userForm", new User() );
        return "registration";
    }

    @RequestMapping( value = "/registration", method = RequestMethod.POST )
    public String registration( @ModelAttribute( "userForm" ) User userForm, BindingResult bindingResult ){
        validator.validate( userForm, bindingResult );
        if( bindingResult.hasErrors() )
            return "registration";
        userService.saveUser( userForm );
        return "redirect:/";
    }

    @RequestMapping( value = "/img", method = RequestMethod.GET )
    public ResponseEntity<byte[]> getImage( @RequestParam String id ) throws IOException{
        return clientController.getImage( id );
    }
}
