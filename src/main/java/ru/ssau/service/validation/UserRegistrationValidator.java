package ru.ssau.service.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ru.ssau.domain.User;
import ru.ssau.domain.UserRegistrationForm;
import ru.ssau.service.UserService;

import java.util.Optional;

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
        User user = ( User ) o;
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "login", "NotEmpty" );
        if( user.getLogin().length() < 6 || user.getLogin().length() > 32 )
            errors.rejectValue( "login", "Size.userForm.login" );
        Optional<User> optional = userService.getUser( user.getLogin() );
        optional.ifPresent( user1 -> errors.rejectValue( "login", "Duplicate.userForm.login" ) );

        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "password", "NotEmpty" );
        if( user.getPassword().length() < 8 || user.getPassword().length() > 32 )
            errors.rejectValue( "password", "Size.userForm.password" );

        // TODO: 02.04.17 Подтверждение пароля
    }

    //    Для клиентской формы
    public boolean validate( UserRegistrationForm userRegistrationForm ){
        // TODO: 02.04.17 Валидация
        return true;
    }
}
