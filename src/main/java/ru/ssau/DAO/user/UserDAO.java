package ru.ssau.DAO.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import ru.ssau.DAO.survey.DeserializeSurveyOptions;
import ru.ssau.domain.User;
import ru.ssau.exceptions.UserNotFoundException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class UserDAO{

    @Autowired
    private Semaphore semaphore;
    @Autowired
    private Environment  environment;
    @Autowired
    private ObjectMapper objectMapper;


    public UserDAO( Semaphore semaphore ){
        this.semaphore = semaphore;
    }

    public void beginTransaction(){
        try{
            semaphore.acquire();
        }catch( InterruptedException e ){
            e.printStackTrace();
        }
    }

    public void endTransaction(){
        semaphore.release();
    }

    public List<User> listUsersByPredicate( Predicate<Path> predicate , Integer limit , DeserializeUserOptions[] userOptions , DeserializeSurveyOptions...surveyOptions )
            throws IOException{
        List<DeserializeUserOptions> userOptions1 = Arrays.asList( userOptions );
        Stream<BDUser> bdUserStream = listByPredicate( predicate ).stream();
        if( userOptions1.contains( DeserializeUserOptions.MADESURVEYS ) )
            bdUserStream = bdUserStream.peek( bdUser -> bdUser.addMadeByUserSurveys( surveyOptions ) );
        if( userOptions1.contains( DeserializeUserOptions.ANSWERS ) )
            bdUserStream = bdUserStream.peek( BDUser::addAnswers );
        return bdUserStream.limit( limit ).map( BDUser::toUser ).collect( Collectors.toList());
    }

    public Optional<User> findUser( String login , DeserializeUserOptions[] userOptions , DeserializeSurveyOptions...surveyOptions  )
            throws IOException{
        return listUsersByPredicate( path -> path.toString().substring( getDirectoryNameLength() ).startsWith( login ) ,
                                     1 , userOptions , surveyOptions ).stream().findFirst();
    }

    public void saveNewUser( User user ) throws IOException{
        saveBDUser( new BDUser( user ) );
    }

    public void updateUser( String login, Consumer<BDUser> consumer ) throws IOException{
        BDUser user = listByPredicate( path ->
            path.toString()
                    .substring( getDirectoryNameLength() )
                    .equals( login ) )
                .stream()
                .findFirst()
                .orElseThrow( () -> new UserNotFoundException( login ) );
        consumer.accept( user );
        deleteUser( user.getLogin() );
        saveBDUser( user );
    }

    public void deleteUser( String login ) throws IOException{
        try{
            Files.delete( Files.list( getDirectory() ).filter(
                    path -> path.toString().substring( getDirectoryNameLength() ).startsWith(
                            login ) ).findFirst().orElseThrow( () -> new UserNotFoundException( login ) ) );
        }catch( NoSuchFileException e ){
            throw new IllegalArgumentException( "Анкета " + login + " не содержится с базе" );
        }
    }

    private List<BDUser> listByPredicate( Predicate<Path> predicate ) throws IOException{
        return Files.list( getDirectory() )
                .filter( predicate )
                .map( path -> {
                    try{
                        return objectMapper.readValue(
                                new String( Files.readAllBytes( path ), Charset.forName( "utf-8" ) ), BDUser.class );
                    }catch( IOException e ){
                        throw new IllegalArgumentException( "Ошибка чтения" );
                    }
                } ).collect( Collectors.toList() );
    }

    private void saveBDUser( BDUser user ) throws IOException{
        try{
            Files.write( Paths.get( getDirectory().toString() + "/" + user.getLogin() ),
                         objectMapper.writeValueAsString( user ).getBytes( Charset.forName( "utf-8" ) ),
                         StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE );
        }catch( FileAlreadyExistsException e ){
            throw new IllegalArgumentException( "Пользователь " + user.getLogin() + " уже содержится" );
        }
    }

    private Path getDirectory(){
        return Paths.get( environment.getProperty( "objectsStorage" ) + environment.getProperty( "userStorage" ) );
    }

    public Integer getDirectoryNameLength(){
        return getDirectory().toString().length() + 1;
    }
}
