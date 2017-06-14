package ru.ssau.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ssau.domain.User;
import ru.ssau.service.filesmanager.FilesManager;
import ru.ssau.transport.UserRegistrationForm;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{


    @Autowired
    private ShaPasswordEncoder passwordEncoder;

    @Autowired
    private FilesManager filesManager;

    @Override
    public Optional<User> getUser( String login ){
        // TODO: 13.06.17
        return null;
    }

    @Override
    public List<User> getUsers(){
        // TODO: 13.06.17
        return null;
    }

    @Override
    public void saveUser( UserRegistrationForm userRegistrationForm ) throws IOException{
        // TODO: 13.06.17
    }

    @Override
    public void saveUser( User userForm ) throws IOException{
        // TODO: 13.06.17
    }

}
