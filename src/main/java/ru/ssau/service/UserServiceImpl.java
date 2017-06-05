package ru.ssau.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ssau.DAO.UserAnswersRepository;
import ru.ssau.DAO.UserRepository;
import ru.ssau.domain.Role;
import ru.ssau.domain.User;
import ru.ssau.domain.UserAnswer;
import ru.ssau.service.filesmanager.FilesManager;
import ru.ssau.transport.UserRegistrationForm;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;

@Service
public class UserServiceImpl implements UserService{

    // TODO: 01.06.17 Работа с БД

    @Autowired
    private ShaPasswordEncoder passwordEncoder;

    @Autowired
    private FilesManager filesManager;

    @Qualifier( "userRepository" )
    @Autowired
    private UserRepository        userRepository;
    @Qualifier( "userAnswersRepository" )
    @Autowired
    private UserAnswersRepository userAnswersRepository;


    @Override
    public Optional<User> getUser( String login ){
        User user = userRepository.findOne( login );
        if( user == null )
            return Optional.empty();
        user.setSurveysDone( userAnswersRepository.getAllAnswersOnSurveysByUserLogin( login ) );
        return Optional.of( user );
    }

    @Override
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    @Override
    public void saveUser( UserRegistrationForm userRegistrationForm ) throws IOException{
        User user = new User();
        user.setLogin( userRegistrationForm.getLogin() );
        user.setName( userRegistrationForm.getName() );
        user.setLastName( userRegistrationForm.getLastName() );
        user.setPassword( passwordEncoder.encodePassword( userRegistrationForm.getPassword(), null ) );
        user.setUserRole( Role.USER );
        if( !userRegistrationForm.getFile().isEmpty() )
            Executors.newFixedThreadPool( 1 ).submit( () -> {
                try{
                    filesManager.saveFile( userRegistrationForm.getFile().getBytes(),
                                           userRegistrationForm.getLogin() + ".png" );
                }catch( IOException e ){
                    e.printStackTrace();
                }
            } );
        userRepository.save( user );
    }

    @Override
    public void saveUser( User userForm ) throws IOException{
        User newUser = new User();
        newUser.setLogin( userForm.getLogin() );
        newUser.setName( userForm.getName() );
        newUser.setLastName( userForm.getLastName() );
        newUser.setPassword( passwordEncoder.encodePassword( userForm.getPassword(), null ) );
        newUser.setUserRole( Role.USER );
        userRepository.save( newUser );
    }

    @Override
    public void addAnswer( UserAnswer answer ){
        userAnswersRepository.save( answer );
    }
}
