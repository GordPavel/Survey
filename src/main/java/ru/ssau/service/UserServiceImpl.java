package ru.ssau.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;
import ru.ssau.domain.UserRoles;
import ru.ssau.service.filesmanager.FilesManager;
import ru.ssau.transport.UserRegistrationForm;

import java.io.IOException;
import java.util.*;

@Service
public class UserServiceImpl implements UserService{


    @Autowired
    private ShaPasswordEncoder passwordEncoder;

    @Autowired
    private FilesManager filesManager;


    private static Map<String, User> users;

    static{
        String adminPasswordHashSha256 = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
        String userPasswordHashSha256  = "04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb";

        users = new HashMap<>();
        User         user = new User( "admin", adminPasswordHashSha256, "name", "lastName", UserRoles.ADMIN );
        List<Survey> list = new ArrayList<>();
        list.add( new Survey( 7 ) );
        list.add( new Survey( 8 ) );
        user.setDoneSurveys( list );
        users.put( "admin", user );
        users.put( "user", new User( "user", userPasswordHashSha256, "name", "lastName", UserRoles.USER ) );
    }

    @Override
    public Optional<User> getUser( String login ){
        if( !users.containsKey( login ) )
            return Optional.empty();
        return Optional.of( users.get( login ) );
    }

    @Override
    public List<User> getUsers(){
        return new ArrayList<>( users.values() );
    }

    @Override
    public void saveUser( UserRegistrationForm userRegistrationForm ) throws IOException{
        User user = new User();
        user.setLogin( userRegistrationForm.getLogin() );
        user.setName( userRegistrationForm.getName() );
        user.setLastName( userRegistrationForm.getLastName() );
        user.setPassword( passwordEncoder.encodePassword( userRegistrationForm.getPassword(), null ) );
        user.setRole( UserRoles.USER );
        if( !userRegistrationForm.getFile().isEmpty() )
            filesManager.saveFile( userRegistrationForm.getFile().getBytes(),
                                   userRegistrationForm.getLogin() + ".png" );
        users.put( user.getLogin(), user );
    }

    @Override
    public void saveUser( User userForm ) throws IOException{
        User newUser = new User();
        newUser.setLogin( userForm.getLogin() );
        newUser.setName( userForm.getName() );
        newUser.setLastName( userForm.getLastName() );
        newUser.setPassword( passwordEncoder.encodePassword( userForm.getPassword(), null ) );
        newUser.setRole( UserRoles.USER );
        users.put( newUser.getLogin(), newUser );
    }

}
