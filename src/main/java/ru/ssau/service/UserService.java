package ru.ssau.service;

import ru.ssau.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserService{

    Optional<User> getUser( String login );

    List<User> getUsers();

    void saveUser( User user );

}
