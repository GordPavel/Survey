package ru.ssau.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ssau.DAO.DatabaseUtils;
import ru.ssau.DAO.enums.DeserializeUserOptions;
import ru.ssau.controller.UserRegistrationForm;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;
import ru.ssau.domain.UserAnswer;
import ru.ssau.domain.UserRoles;
import ru.ssau.exceptions.UserNotFoundException;
import ru.ssau.service.filesmanager.FilesManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/***
 * class to connect user request and database
 */
@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private ShaPasswordEncoder passwordEncoder;
    @Autowired
    private FilesManager       filesManager;
    @Autowired
    private DatabaseUtils      databaseUtils;

    @Override
    public Optional<User> getUser( String login, DeserializeUserOptions... options ) throws InterruptedException{
        try{
            databaseUtils.beginTransaction();
            Optional<User> user = databaseUtils.findUser( login, options );
            databaseUtils.endTransaction();
            return user;
        }catch( IOException e ){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void deleteUser( String login ) throws InterruptedException{
        try{
            databaseUtils.beginTransaction();
            databaseUtils.deleteUser( login );
            databaseUtils.endTransaction();
        }catch( IOException e ){
            e.printStackTrace();
        }
    }

    @Override
    public List<User> getUsers( DeserializeUserOptions... options ) throws InterruptedException{
        try{
            databaseUtils.beginTransaction();
            List<User> users = databaseUtils.listAllUsers( options );
            databaseUtils.endTransaction();
            return users;
        }catch( IOException e ){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Survey> getDoneSurveysByLogin( String login ) throws InterruptedException{
        return getUser( login, DeserializeUserOptions.ANSWERS ).orElseThrow(
                () -> new UserNotFoundException( login ) ).getAnswers().stream().map( UserAnswer::getSurvey ).collect(
                Collectors.toList() );
    }

    @Override
    public List<Survey> getMadeSurveysByLogin( String login ) throws InterruptedException{
        return getUser( login, DeserializeUserOptions.MADESURVEYS ).orElseThrow(
                () -> new UserNotFoundException( login ) ).getMadeSurveys();
    }

    @Override
    public void saveUser( UserRegistrationForm userRegistrationForm ) throws InterruptedException{
        User user = new User();
        user.setLogin( userRegistrationForm.getLogin() );
        user.setName( userRegistrationForm.getName() );
        user.setPassword( userRegistrationForm.getPassword() );
        user.setLastName( userRegistrationForm.getLastName() );
        user.setRole( UserRoles.USER );
        if( !userRegistrationForm.getFile().isEmpty() ) try{
            filesManager.saveFile( userRegistrationForm.getFile().getBytes(), userRegistrationForm.getLogin() );
        }catch( IOException e ){
            e.printStackTrace();
        }
        saveUser( user );
    }

    @Override
    public void saveNewUserAnswer( UserAnswer userAnswer ) throws InterruptedException{
        try{
            databaseUtils.beginTransaction();
            databaseUtils.saveNewUserAnswer( userAnswer );
            databaseUtils.endTransaction();
        }catch( IOException e ){
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUserAnswer( Integer id, String login ) throws InterruptedException{
        try{
            databaseUtils.beginTransaction();
            databaseUtils.deleteUserAnswer( id, login );
            databaseUtils.endTransaction();
        }catch( IOException e ){
            e.printStackTrace();
        }
    }

    @Override
    public void saveUser( User userForm ) throws InterruptedException{
        try{
            userForm.setRole( UserRoles.USER );
            userForm.setPassword( passwordEncoder.encodePassword( userForm.getPassword(), null ) );
            databaseUtils.beginTransaction();
            databaseUtils.saveUser( userForm );
            databaseUtils.endTransaction();
        }catch( IOException e ){
            e.printStackTrace();
        }
    }

    @Override
    public void changeUserName( String login, String newName ){
        databaseUtils.updateUser( login, bdUser -> bdUser.setName( newName ) );
    }

    @Override
    public void changeUserLastName( String login, String newLastName ){
        databaseUtils.updateUser( login, bdUser -> bdUser.setLastName( newLastName ) );
    }

    @Override
    public Boolean hasUserAnsweredOnSurvey( UserAnswer userAnswer ){
        return databaseUtils.isUserAnswerAlreadyExists( userAnswer );
    }
}
