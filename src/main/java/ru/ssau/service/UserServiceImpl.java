package ru.ssau.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ssau.domain.User;
import ru.ssau.domain.UserRoles;
import ru.ssau.service.filesmanager.FilesManager;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private ShaPasswordEncoder passwordEncoder;

    @Autowired
    private FilesManager filesManager;

    @Override
    public Optional<User> getUser( String login ){
        User user = new User();
        user.setLogin( login );
        user.setPassword( passwordEncoder.encodePassword( "1234", null ) );
        switch( login ){
            case "admin":
                user.setRole( UserRoles.ADMIN );
                break;
            case "user":
                user.setRole( UserRoles.USER );
            case "anon111":
                return Optional.empty();
            default:
                user.setRole( UserRoles.ANONYMOUS );
        }
        return Optional.of( user );
    }

    @Override
    public List<User> getUsers(){
        return Stream.iterate( new User( "user" + 1 ), new UnaryOperator<User>(){
            int i = 1;

            @Override
            public User apply( User user ){
                return new User( "user" + ( ++i ) );
            }
        } ).limit( 5 ).collect( Collectors.toList() );
    }

    @Override
    public void saveUser( User user ){
        System.out.println( user.getLogin() );
        System.out.println( passwordEncoder.encodePassword( user.getPassword(), null ) );
        System.out.println( UserRoles.USER.name() );
        try{
            filesManager.saveFile( user.getFile().getBytes(), user.getLogin() + ".jpeg" );
        }catch( IOException e ){
            System.out.println(
                    String.format( "Ошибка записи файла %s%s.jpeg", filesManager.getFilesDir(), user.getLogin() ) );
            e.printStackTrace();
        }
    }
}
