package ru.ssau.service;

import ru.ssau.DAO.enums.DeserializeUserOptions;
import ru.ssau.domain.User;
import ru.ssau.transport.UserRegistrationForm;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService{

    Optional<User> getUser( String login , DeserializeUserOptions... options );

    List<User> getUsers( DeserializeUserOptions ... options  );

    void saveUser( UserRegistrationForm user ) throws IOException;

    void saveUser( User user ) throws IOException;

}
