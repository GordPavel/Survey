package ru.ssau.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ssau.DAO.DAO;
import ru.ssau.DAO.enums.DeserializeUserOptions;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;
import ru.ssau.domain.UserAnswer;
import ru.ssau.domain.UserRoles;
import ru.ssau.exceptions.UserNotFoundException;
import ru.ssau.service.filesmanager.FilesManager;
import ru.ssau.controller.UserRegistrationForm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private ShaPasswordEncoder passwordEncoder;
    @Autowired
    private FilesManager       filesManager;
    @Autowired
    private DAO                dao;

    @Override
    public Optional<User> getUser( String login, DeserializeUserOptions... options ){
        try{
            dao.beginTransaction();
            Optional<User> user = dao.findUser( login, options );
            dao.endTransaction();
            return user;
        }catch( IOException | InterruptedException e ){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<User> getUsers( DeserializeUserOptions... options ){
        try{
            dao.beginTransaction();
            List<User> users = dao.listAllUsers( options );
            dao.endTransaction();
            return users;
        }catch( IOException | InterruptedException e ){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Survey> getDoneSurveysByLogin( String login ){
        return getUser( login, DeserializeUserOptions.ANSWERS ).orElseThrow(
                () -> new UserNotFoundException( login ) ).getAnswers().stream().map( UserAnswer::getSurvey ).collect(
                Collectors.toList() );
    }

    @Override
    public List<Survey> getMadeSurveysByLogin( String login ){
        return getUser( login, DeserializeUserOptions.MADESURVEYS ).orElseThrow(
                () -> new UserNotFoundException( login ) ).getMadeSurveys();
    }

    @Override
    public void saveUser( UserRegistrationForm userRegistrationForm ) throws IOException{
        User user = new User();
        user.setLogin( userRegistrationForm.getLogin() );
        user.setName( userRegistrationForm.getName() );
        user.setPassword( userRegistrationForm.getPassword() );
        user.setLastName( userRegistrationForm.getLastName() );
        user.setRole( UserRoles.USER );
        if( !userRegistrationForm.getFile().isEmpty() )
            filesManager.saveFile( userRegistrationForm.getFile().getBytes(),
                                   userRegistrationForm.getLogin() + ".png" );
        saveUser( user );
    }

    @Override
    public void saveNewUserAnswer( UserAnswer userAnswer ) throws IOException{
        try{
            dao.beginTransaction();
            dao.saveNewUserAnswer( userAnswer );
            dao.endTransaction();
        }catch( InterruptedException e ){
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUserAnswer( Integer id, String login ) throws IOException{
        try{
            dao.beginTransaction();
            dao.deleteUserAnswer( id, login );
            dao.endTransaction();
        }catch( InterruptedException e ){
            e.printStackTrace();
        }
    }

    @Override
    public void saveUser( User userForm ) throws IOException{
        try{
            userForm.setRole( UserRoles.USER );
            userForm.setPassword( passwordEncoder.encodePassword( userForm.getPassword(), null ) );
            dao.beginTransaction();

            dao.saveUser( userForm );
            dao.endTransaction();
        }catch( InterruptedException e ){
            e.printStackTrace();
        }
    }
}
