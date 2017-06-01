package ru.ssau.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ssau.domain.User;
import ru.ssau.service.filesmanager.FilesManager;
import ru.ssau.transport.UserRegistrationForm;

import java.io.IOException;
import java.util.*;

@Service
public class UserServiceImpl implements UserService{

    // TODO: 01.06.17 Работа с БД

    @Autowired
    private ShaPasswordEncoder passwordEncoder;

    @Autowired
    private FilesManager filesManager;

    @Override
    public Optional<User> getUser( String login ){
        return null;
    }

    @Override
    public List<User> getUsers(){
        return null;
    }

    @Override
    public void saveUser( UserRegistrationForm userRegistrationForm ) throws IOException{
        return ;
    }

    @Override
    public void saveUser( User userForm ) throws IOException{
        return;
    }

}
