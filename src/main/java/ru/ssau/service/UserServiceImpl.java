package ru.ssau.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ssau.DAO.DAO;
import ru.ssau.DAO.enums.DeserializeUserOptions;
import ru.ssau.domain.User;
import ru.ssau.domain.UserRoles;
import ru.ssau.service.filesmanager.FilesManager;
import ru.ssau.transport.UserRegistrationForm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private ShaPasswordEncoder passwordEncoder;
    @Autowired
    private FilesManager filesManager;
    @Autowired
    private DAO dao;


    @Override
    public Optional<User> getUser( String login , DeserializeUserOptions ... options ){
        try{
            return dao.findUser( login , options );
        }catch( IOException e ){
            return Optional.empty();
        }
    }

    @Override
    public List<User> getUsers( DeserializeUserOptions ... options ){
        try{
            return dao.listAllUsers( options );
        }catch( IOException e ){
            return new ArrayList<>();
        }
    }

    @Override
    public void saveUser( UserRegistrationForm userRegistrationForm ) throws IOException{
        User user = new User(  );
        user.setLogin( userRegistrationForm.getLogin() );
        user.setPassword( passwordEncoder.encodePassword( userRegistrationForm.getPasswordRepeat() , null ) );
        user.setName( userRegistrationForm.getName() );
        user.setLastName( userRegistrationForm.getLastName() );
        user.setRole( UserRoles.USER );
        if( !userRegistrationForm.getFile().isEmpty() )
            filesManager.saveFile( userRegistrationForm.getFile().getBytes(),
                                   userRegistrationForm.getLogin() + ".png" );
        dao.saveUser( user );
    }

    @Override
    public void saveUser( User userForm ) throws IOException{
        dao.saveUser( userForm );
    }

}
