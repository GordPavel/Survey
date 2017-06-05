package ru.ssau.service;

import ru.ssau.domain.User;
import ru.ssau.domain.UserAnswer;
import ru.ssau.transport.UserRegistrationForm;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService{

    Optional<User> getUser( String login );

    List<User> getUsers();

    void saveUser( UserRegistrationForm user ) throws IOException;

    void saveUser( User user ) throws IOException;

    void addAnswer( UserAnswer answer );

}
