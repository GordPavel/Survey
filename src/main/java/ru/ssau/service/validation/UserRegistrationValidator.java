package ru.ssau.service.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ru.ssau.controller.UserRegistrationForm;
import ru.ssau.domain.User;
import ru.ssau.service.UserService;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserRegistrationValidator implements Validator{

    @Autowired
    private UserService userService;

    @Override
    public boolean supports( Class<?> aClass ){
        return User.class.equals( aClass );
    }

    @Override
    public void validate( Object o, Errors errors ){
        UserRegistrationForm user = ( UserRegistrationForm ) o;

        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "name", "NotEmpty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "lastName", "NotEmpty" );

        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "login", "NotEmpty" );
        if( user.getLogin().length() < 6 || user.getLogin().length() > 32 )
            errors.rejectValue( "login", "Size.userForm.login" );

        try{
            Optional<User> optional = userService.getUser( user.getLogin() );
            optional.ifPresent( user1 -> errors.rejectValue( "login", "Duplicate.userForm.login" ) );
        }catch( InterruptedException e ){
            errors.rejectValue( "login" , "userForm.downloadError" );
        }

        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "password", "NotEmpty" );
        if( user.getPassword().length() < 8 || user.getPassword().length() > 32 )
            errors.rejectValue( "password", "Size.userForm.password" );

        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "passwordRepeat", "NotEmpty" );
        if( !user.getPasswordRepeat().equals( user.getPassword() ) )
            errors.rejectValue( "passwordRepeat", "Diff.userForm.password" );

        if( !user.getFile().isEmpty() ){
            String type = new LinkedList<>(
                    Arrays.asList( user.getFile().getOriginalFilename().split( "\\." ) ) ).getLast().toLowerCase();
            if( !( type.equals( "jpg" ) || type.equals( "png" ) ) )
                errors.rejectValue( "file", "File.notJPG" );
        }
    }

//    0: все хорошо
//    1: логин занят
//    2: ошибка длины логина
//    3: ошибка длины пароля

    //    Для клиентской формы
    public Integer validate( User user ) throws InterruptedException{
        List<String> usersLogins = userService.getUsers().stream().map( User::getLogin ).collect( Collectors.toList() );
        if( usersLogins.contains( user.getLogin() ) ) return 1;
        if( user.getLogin().isEmpty() || user.getLogin().length() < 6 || user.getLogin().length() > 32 ){return 2;}
        if( user.getPassword().length() < 6 || user.getPassword().length() > 32 ) return 3;
        return 0;
    }
}
