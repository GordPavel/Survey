package ru.ssau.DAO.survey;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import ru.ssau.DAO.user.DeserializeUserOptions;
import ru.ssau.domain.Survey;
import ru.ssau.exceptions.SurveyNotFoundException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class SurveyDAO{

    @Autowired
    private Semaphore    semaphore;
    @Autowired
    private Environment  environment;
    @Autowired
    private ObjectMapper objectMapper;

    public void beginTransaction() throws InterruptedException{
        semaphore.acquire();
    }

    public void endTransaction(){
        semaphore.release();
    }

    public List<Survey> listSurveysByPredicate( Predicate<Path> predicate, SurveysSort sort, Integer limit,
                                                DeserializeSurveyOptions[] surveyOptions,
                                                DeserializeUserOptions[] userOptions ) throws IOException{
        List<DeserializeSurveyOptions> surveyOptions1 = Arrays.asList( surveyOptions );
        if( sort == SurveysSort.ANSWERS && !surveyOptions1.contains( DeserializeSurveyOptions.ANSWERS ) )
            surveyOptions1.add( DeserializeSurveyOptions.ANSWERS );
        Stream<BDSurvey> bdSurveyStream = listByPredicate( predicate ).stream();
        if( surveyOptions1.contains( DeserializeSurveyOptions.CREATOR ) )
            bdSurveyStream = bdSurveyStream.peek( bdSurvey -> bdSurvey.addCreator( userOptions ) );
        if( surveyOptions1.contains( DeserializeSurveyOptions.ANSWERS ) )
            bdSurveyStream = bdSurveyStream.peek( BDSurvey::addAnswers );
        if( surveyOptions1.contains( DeserializeSurveyOptions.CATEGORY ) )
            bdSurveyStream = bdSurveyStream.peek( BDSurvey::addCategory );
        if( surveyOptions1.contains( DeserializeSurveyOptions.QUESTIONS ) )
            bdSurveyStream = bdSurveyStream.peek( BDSurvey::addQuestions );
        Comparator<Survey> comparator;
        if( sort == SurveysSort.TIME ) comparator = ( o1, o2 ) -> o2.getDate().compareTo( o1.getDate() );
        else comparator = ( o1, o2 ) -> Integer.compare( o2.getUsersDone(), o1.getUsersDone() );
        return bdSurveyStream.map( BDSurvey::toSurvey ).sorted( comparator ).limit( limit ).collect(
                Collectors.toList() );
    }

    public Optional<Survey> findSurvey( Integer id , DeserializeSurveyOptions[] surveyOptions,
                                        DeserializeUserOptions[] userOptions ) throws IOException{
        return listSurveysByPredicate( path -> path.toString()
                                               .substring( getSurveyDirectoryNameLength() )
                                               .startsWith( id.toString() ),
                                       SurveysSort.TIME,
                                       1,
                                       surveyOptions,
                                       userOptions )
                .stream()
                .findFirst();
    }

    public void saveNewSurvey( Survey survey ) throws IOException{
        // TODO: 15.06.17 Добавить сохранение вопросов
        if( survey.getCreator() == null || survey.getCategory() == null || survey.getDate() == null )
            throw new IllegalArgumentException( "Нельзя сохранить такую анкету" );
        survey.setId(
                Files.list( getSurveyDirectory() )
                        .filter( path -> !path.toString().substring( getSurveyDirectoryNameLength() ).equals( ".DS_Store" ) )
                        .mapToInt( path -> {
                            String fileName = path.toString().substring( getSurveyDirectoryNameLength() );
                            return Integer.parseInt( fileName.substring( 0, fileName.indexOf( "_" ) ) );
                        } )
                        .max()
                        .orElse( 0 ) + 1 );
        try{
            saveDBSurvey( new BDSurvey( survey ) );
        }catch( FileAlreadyExistsException e ){
            throw new IllegalArgumentException( "Анкета " + survey.getId() + " уже содержится с базе" );
        }
    }

    public void updateSurvey( Integer id, Consumer<BDSurvey> consumer ) throws IOException{
        BDSurvey bdSurvey = listByPredicate( path -> path.toString().substring( getSurveyDirectoryNameLength() )
                .startsWith( id.toString() ) )
                .stream().findFirst().orElseThrow( () -> new SurveyNotFoundException( id ) );
        consumer.accept( bdSurvey );
        bdSurvey.setDate( new Date() );
        deleteSurvey( bdSurvey.getId() );
        saveDBSurvey( bdSurvey );
    }

    public void deleteSurvey( Integer id ) throws IOException{
        try{
            Files.delete( Files.list( getSurveyDirectory() )
                                  .filter( path -> path.toString()
                                          .substring( getSurveyDirectoryNameLength() )
                                          .startsWith( id.toString() ) )
                                  .findFirst().orElseThrow( () -> new SurveyNotFoundException( id ) ) );
        }catch( NoSuchFileException e ){
            throw new IllegalArgumentException( "Анкета " + id + " не содержится с базе" );
        }
    }

    private List<BDSurvey> listByPredicate( Predicate<Path> predicate ) throws IOException{
        return Files.list( getSurveyDirectory() ).filter( predicate ).map( path -> {
            try{
                return objectMapper.readValue( new String( Files.readAllBytes( path ), Charset.forName( "utf-8" ) ),
                                               BDSurvey.class );
            }catch( IOException e ){
                throw new IllegalArgumentException( "Ошибка чтения" );
            }
        } ).collect( Collectors.toList() );
    }

    private void saveDBSurvey( BDSurvey bdSurvey ) throws IOException{
        try{
            Files.write( Paths.get(
                    getSurveyDirectory().toString() + "/" + bdSurvey.getId() + "_" + bdSurvey.getCreator() + "_" +
                    bdSurvey.getCategory() ),
                         objectMapper.writeValueAsString( bdSurvey ).getBytes( Charset.forName( "utf-8" ) ),
                         StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE );
        }catch( FileAlreadyExistsException e ){
            throw new IllegalArgumentException( "Анкета " + bdSurvey.getId() + " уже содержится" );
        }
    }

    private Path getSurveyDirectory(){
        return Paths.get( environment.getProperty( "objectsStorage" ) + environment.getProperty( "surveyStorage" ) );
    }

    public Integer getSurveyDirectoryNameLength(){
        return getSurveyDirectory().toString().length() + 1;
    }
}
