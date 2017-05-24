package ru.ssau.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ssau.domain.User;
import ru.ssau.domain.UserRoles;
import ru.ssau.service.filesmanager.FilesManager;
import ru.ssau.transport.UserRegistrationForm;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private ShaPasswordEncoder passwordEncoder;

    @Autowired
    private FilesManager filesManager;

    private static Map<String, User> users;

    static{
        String adminPasswordHashSha_256 = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
        String userPasswordHashSha_256  = "04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb";

        users = new HashMap<>();
        users.put( "admin", new User( "admin", adminPasswordHashSha_256, "name", "lastName", UserRoles.ADMIN, "1" ) );
        users.put( "user", new User( "user", userPasswordHashSha_256, "name", "lastName", UserRoles.USER ) );
    }

    @Override
    public Optional<User> getUser( String login ){
        if( !users.containsKey( login ) )
            return Optional.empty();
        return Optional.of( users.get( login ) );
    }

    @Override
    public List<User> getUsers(){
        return users.values().stream().collect( Collectors.toList() );
    }

    @Override
    public void saveUser( UserRegistrationForm userRegistrationForm ){
        User user = new User();
        user.setLogin( userRegistrationForm.getLogin() );
        user.setName( userRegistrationForm.getName() );
        user.setLastName( userRegistrationForm.getLastName() );
        user.setPassword( passwordEncoder.encodePassword( userRegistrationForm.getPassword(), null ) );
        user.setRole( UserRoles.USER );
        if( !userRegistrationForm.getFile().isEmpty() ){
            user.setFileId( userRegistrationForm.getLogin() );
            try{
                filesManager.saveFile( userRegistrationForm.getFile().getBytes(),
                                       userRegistrationForm.getLogin() + ".jpeg" );
            }catch( IOException e ){
                System.out.println( String.format( "Ошибка записи файла %s%s.jpeg", filesManager.getFilesDir(),
                                                   userRegistrationForm.getLogin() ) );
                e.printStackTrace();
            }
        }
        users.put( user.getLogin(), user );
    }
}
