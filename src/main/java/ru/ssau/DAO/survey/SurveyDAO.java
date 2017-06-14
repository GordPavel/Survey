package ru.ssau.DAO.survey;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import ru.ssau.domain.Survey;
import ru.ssau.exceptions.SurveyNotFoundException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Date;
import java.util.concurrent.Semaphore;

@Repository
public class SurveyDAO{

    @Autowired
    private Semaphore semaphore;

    @Autowired
    private Environment environment;

    public void beginTransaction() throws InterruptedException{
        System.out.println( Thread.currentThread().getName() + " пришел" );
        semaphore.acquire();
    }

    public void endTransaction(){
        semaphore.release();
        System.out.println( Thread.currentThread().getName() + " ушел" );
    }

    public Survey findById( Integer id, DeserializeSurveyOption... options ) throws IOException{
        System.out.println( Thread.currentThread().getName() + " начал выполнение" );
        try{
            Thread.sleep( 10 * 1000 );
        }catch( InterruptedException e ){
            e.printStackTrace();
        }
        return findDBSurvey( id ).toSurvey( options );
    }

    public void saveNewSurvey( Survey survey ) throws IOException{
        if( survey.getCreator() == null || survey.getCategory() == null || survey.getDate() == null )
            throw new IllegalArgumentException( "Нельзя сохранить такую анкету" );
        survey.setId( Files.list( getDirectory() ).filter(
                path -> !path.toString().substring( getDirectoryNameLength() ).equals( ".DS_Store" ) ).mapToInt(
                path -> {
                    String fileName = path.toString().substring( getDirectoryNameLength() );
                    return Integer.parseInt( fileName.substring( 0, fileName.indexOf( "_" ) ) );
                } ).max().orElse( 0 ) + 1 );
        try{
            Files.write( Paths.get(
                    getDirectory().toString() + "/" + survey.getId() + "_" + survey.getCreator().getLogin() + "_" +
                    survey.getCategory().getName() ),
                         new ObjectMapper().writeValueAsString( new DBSurvey( survey ) ).getBytes(
                                 Charset.forName( "utf-8" ) ), StandardOpenOption.CREATE_NEW,
                         StandardOpenOption.WRITE );
        }catch( FileAlreadyExistsException e ){
            throw new IllegalArgumentException( "Анкета " + survey.getName() + " уже содержится" );
        }
    }

    public void updateName( Integer id, String name ) throws IOException{
        DBSurvey dbSurvey = findDBSurvey( id );
        dbSurvey.setName( name );
        dbSurvey.setDate( new Date() );
        deleteSurvey( dbSurvey.getId() );
        saveDBSurvey( dbSurvey );
    }

    public void updateComment( Integer id, String comment ) throws IOException{
        DBSurvey dbSurvey = findDBSurvey( id );
        dbSurvey.setComment( comment );
        dbSurvey.setDate( new Date() );
        deleteSurvey( dbSurvey.getId() );
        saveDBSurvey( dbSurvey );
    }

    public void deleteSurvey( Integer id ) throws IOException{
        try{
            Files.delete( Files.list( getDirectory() ).filter(
                    path -> path.toString().substring( getDirectoryNameLength() ).startsWith(
                            id.toString() ) ).findFirst().orElseThrow( () -> new SurveyNotFoundException( id ) ) );
        }catch( NoSuchFileException e ){
            throw new IllegalArgumentException( "Анкета " + id + " не содержится с базе" );
        }
    }

    private DBSurvey findDBSurvey( Integer id ) throws IOException{
        return Files.list( getDirectory() ).filter(
                path -> path.toString().substring( getDirectoryNameLength() ).startsWith( id.toString() ) ).map(
                path -> {
                    try{
                        return new ObjectMapper().readValue(
                                new String( Files.readAllBytes( path ), Charset.forName( "utf-8" ) ), DBSurvey.class );
                    }catch( IOException e ){
                        throw new IllegalArgumentException( "Ошибка чтения" );
                    }
                } ).findFirst().orElseThrow( () -> new SurveyNotFoundException( id ) );
    }

    private void saveDBSurvey( DBSurvey dbSurvey ) throws IOException{
        Files.write( Paths.get( getDirectory().toString() + "/" + dbSurvey.getId() + "_" + dbSurvey.getCreator() + "_" +
                                dbSurvey.getCategory() ),
                     new ObjectMapper().writeValueAsString( dbSurvey ).getBytes( Charset.forName( "utf-8" ) ),
                     StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE );
    }

    private Path getDirectory(){
        return Paths.get( "/survey/survey/" );
    }

    private Integer getDirectoryNameLength(){
        return getDirectory().toString().length() + 1;
    }

}
