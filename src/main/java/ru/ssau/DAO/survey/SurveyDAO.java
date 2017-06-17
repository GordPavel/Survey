package ru.ssau.DAO.survey;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import ru.ssau.DAO.user.UserDAO;
import ru.ssau.domain.Answer;
import ru.ssau.domain.Category;
import ru.ssau.domain.Question;
import ru.ssau.domain.Survey;
import ru.ssau.exceptions.CategoryNotFoundException;
import ru.ssau.exceptions.SurveyNotFoundException;
import ru.ssau.exceptions.UserNotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class SurveyDAO{

    private final Semaphore    semaphore;
    private final Environment  environment;
    private final ObjectMapper objectMapper;
    private final UserDAO      userDAO;

    @Autowired
    public SurveyDAO( Semaphore semaphore, Environment environment, ObjectMapper objectMapper, UserDAO userDAO ){
        this.semaphore = semaphore;
        this.environment = environment;
        this.objectMapper = objectMapper;
        this.userDAO = userDAO;
    }

    public void beginTransaction() throws InterruptedException{
        semaphore.acquire();
    }

    public void endTransaction(){
        semaphore.release();
    }

    public List<Survey> listSurveysByPredicate( Predicate<Path> predicate, SurveysSort sort, Integer limit,
                                                DeserializeSurveyOptions... surveyOptions ) throws IOException{
        List<DeserializeSurveyOptions> surveyOptions1 = Arrays.asList( surveyOptions );
        if( sort == SurveysSort.ANSWERS && !surveyOptions1.contains( DeserializeSurveyOptions.ANSWERS ) )
            surveyOptions1.add( DeserializeSurveyOptions.ANSWERS );
        List<BDSurvey> bdSurveys = listByPredicate( predicate );
        if( surveyOptions1.contains( DeserializeSurveyOptions.CREATOR ) )
            bdSurveys.forEach( bdSurvey -> {
                try{
                    bdSurvey.setUserCreator( userDAO.findUser( bdSurvey.getCreator() ).orElseThrow(
                            () -> new UserNotFoundException( bdSurvey.getCreator() ) ) );
                }catch( IOException e ){
                    throw new IllegalArgumentException( "Не удалось загразить пользователя " + bdSurvey.getCreator() );
                }
            } );
        if( surveyOptions1.contains( DeserializeSurveyOptions.ANSWERS ) )
            bdSurveys.forEach( bdSurvey -> {
                // TODO: 16.06.17
                bdSurvey.setAnswers( null );
            } );
        if( surveyOptions1.contains( DeserializeSurveyOptions.CATEGORY ) )
            bdSurveys.forEach( bdSurvey -> {
                try{
                    bdSurvey
                            .setCategory( Files.list( getCategoriesDirectory() )
                                                  .filter( path -> path.toString()
                                                          .substring( getCategoriesDirectoryNameLength() )
                                                          .equals( bdSurvey.getCategoryName() ) )
                                                  .map( path -> {
                                                        try{
                                                            BDCategory bdCategory = new ObjectMapper().readValue(
                                                                    new String( Files.readAllBytes( path ), Charset.forName( "utf-8" ) ),
                                                                    BDCategory.class );
                                                            return bdCategory.toCategory();
                                                        }catch( IOException e ){
                                                            throw new IllegalArgumentException( "Ошибка чтения категорий" );
                                                        }
                                                    } )
                                                  .findFirst().orElseThrow( () -> new CategoryNotFoundException( bdSurvey.getCategoryName() ) ) );
                }catch( IOException e ){
                    throw new IllegalArgumentException( "Не удалось загрузить вопросы к анкете " + bdSurvey.getId() );
                }
            } );
        if( surveyOptions1.contains( DeserializeSurveyOptions.QUESTIONS ) )
            bdSurveys.forEach( bdSurvey -> {
                try{
                    List<Question> questions = Files
                            .list( getQuestionsDirectory() )
                            .filter( path -> {
                                String str = path.toString().substring( getQuestionsDirectoryNameLength() );
                                return str.substring( str.indexOf( "_" ) + 1 ).equals( bdSurvey.getId().toString() );
                            } )
                            .map( path -> {
                                try{
                                    return objectMapper.readValue( new String( Files.readAllBytes( path ), Charset.forName( "utf-8" ) ),
                                                                   BDQuestion.class ).toQuestion();
                                }catch( IOException e ){
                                    throw new IllegalArgumentException( "Ошибка чтения вопросов" );
                                }
                            } )
                            .peek( question -> {
                                try{
                                    question.setAnswers( Files.list( getAnswersDirectory() )
                                                                 .filter( path -> !path.equals( Paths.get( "/survey/answer/.DS_Store" ) ) )
                                                                 .filter( path -> {
                                                                     String str = path.toString().substring( getAnswersDirectoryNameLength() );
                                                                     return str.substring( str.indexOf( "_" ) + 1 ).equals(
                                                                             question.getId().toString() + "_" + bdSurvey.getId().toString() );
                                                                 } )
                                                                 .map( path -> {
                                                                     try{
                                                                         return objectMapper.readValue(
                                                                                 new String( Files.readAllBytes( path ), Charset.forName( "utf-8" ) ),
                                                                                 BDAnswer.class ).toAnswer();
                                                                     }catch( IOException e ){
                                                                         throw new IllegalArgumentException( "Ошибка чтения ответов" );
                                                                     }
                                                                 } )
                                                                 .collect( Collectors.toList() ) );
                                }catch( IOException e ){
                                    throw new IllegalArgumentException( "Не удалось загрузить ответы к вопросу" + question.getId() );
                                }
                            } )
                            .collect( Collectors.toList() );
                    bdSurvey.setQuestions( questions );
                }catch( IOException e ){
                    throw new IllegalArgumentException( "Не удалось загрузить вопросы к анкете " + bdSurvey.getId() );
                }
            } );
        Comparator<Survey> comparator;
        if( sort == SurveysSort.TIME ) comparator = ( o1, o2 ) -> o2.getDate().compareTo( o1.getDate() );
        else comparator = ( o1, o2 ) -> Integer.compare( o2.getUsersDone(), o1.getUsersDone() );
        return bdSurveys.stream().map( BDSurvey::toSurvey ).sorted( comparator ).limit( limit ).collect(
                Collectors.toList() );
    }

    public Optional<Survey> findSurvey( Integer id, DeserializeSurveyOptions ... surveyOptions ) throws IOException{
        return listSurveysByPredicate( path -> {
            String str = path.toString().substring( getAnswersDirectoryNameLength() );
            return str.substring( 0 , str.indexOf( "_" ) ).equals( id.toString() );
        }, SurveysSort.TIME, 1, surveyOptions ).stream().findFirst();
    }

    public Integer saveNewSurvey( Survey survey ) throws IOException{
        if( survey.getCreator() == null || survey.getCategory() == null || survey.getDate() == null )
            throw new IllegalArgumentException( "Нельзя сохранить такую анкету" );
        Integer id = Files
                             .list( getSurveyDirectory() )
                             .filter( path -> !path.toString().substring( getSurveyDirectoryNameLength() ).equals( ".DS_Store" ) )
                             .mapToInt(
                                path -> {
                                    String fileName = path.toString().substring( getSurveyDirectoryNameLength() );
                                    return Integer.parseInt( fileName.substring( 0, fileName.indexOf( "_" ) ) );
                                } )
                             .max()
                             .orElse( 0 ) + 1;
        survey.setId( id );
        try{
            saveDBSurvey( new BDSurvey( survey ) );
        }catch( FileAlreadyExistsException e ){
            throw new IllegalArgumentException( "Анкета " + survey.getId() + " уже содержится с базе" );
        }
        survey.getQuestions().stream()
//                 Раздать всем id
                .peek( new Consumer<Question>(){
                    int i = 0;
                    @Override
                    public void accept( Question question ){
                        question.setId( i++ );
                        question.getAnswers().forEach( new Consumer<Answer>(){
                            int i = 0;

                            @Override
                            public void accept( Answer answer ){
                                answer.setId( i++ );
                            }
                        } );
                    }
                } )
//                Сохранить в базу
                .peek( question -> question
                        .getAnswers().stream()
                        .map( BDAnswer::new )
                        .forEach( bdAnswer -> {
                                try{
                                    Files.write( Paths.get(
                                            getAnswersDirectory() + "/" + bdAnswer.id + "_" + question.getId() + "_" + survey.getId() ),
                                                 objectMapper.writeValueAsString( bdAnswer ).getBytes( "utf-8" ),
                                                 StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE );
                                }catch( FileAlreadyExistsException e ){
                                    throw new IllegalArgumentException(
                                            "Ответ " + bdAnswer.getId() + "_" + question.getId() + "_" + survey.getId() +
                                            " уже содержится с базе" );
                                }catch( IOException e ){
                                    e.printStackTrace();
                                }
                        } ) )
                .map( BDQuestion::new )
                .forEach( bdQuestion -> {
                            try{
                                Files.write( Paths.get( getQuestionsDirectory() + "/" + bdQuestion.getId() + "_" + survey.getId() ),
                                             objectMapper.writeValueAsString( bdQuestion ).getBytes( "utf-8" ),
                                             StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE );
                            }catch( FileAlreadyExistsException e ){
                                throw new IllegalArgumentException(
                                        "Вопрос " + bdQuestion.getId() + "_" + survey.getId() + " уже содержится в базе" );
                            }catch( IOException e ){
                                e.printStackTrace();
                            }
        } );
        if( isCategoryNew( survey.getCategory() ) ) saveCategory( new BDCategory( survey.getCategory() ) );
        return id;
    }

    public void updateSurvey( Integer id, Consumer<BDSurvey> consumer ) throws IOException{
        BDSurvey bdSurvey = listByPredicate( path -> {
            String str = path.toString().substring( getAnswersDirectoryNameLength() );
            return str.substring( 0 , str.indexOf( "_" ) ).equals( id.toString() );
        } )
                .stream()
                .findFirst()
                .orElseThrow( () -> new SurveyNotFoundException( id ) );
        consumer.accept( bdSurvey );
        bdSurvey.setDate( new Date() );
        deleteSurvey( bdSurvey.getId() );
        saveDBSurvey( bdSurvey );
    }

    public void deleteSurvey( Integer id ) throws IOException{
        try{
            Files.delete( Files.list( getSurveyDirectory() )
                                  .filter( path -> {
                                      String str = path.toString().substring( getAnswersDirectoryNameLength() );
                                      return str.substring( 0 , str.indexOf( "_" ) ).equals( id.toString() );
                                  } )
                                  .findFirst()
                                  .orElseThrow( () -> new SurveyNotFoundException( id ) ) );
            Files.list( getQuestionsDirectory() )
                    .filter( path -> {
                        String str = path.toString().substring( getQuestionsDirectoryNameLength() );
                        return str.substring( str.indexOf( "_" ) + 1 ).equals( id.toString() );
                    } )
                    .forEach( path -> {
                        try{
                            Files.delete( path );
                        }catch( FileNotFoundException e ){
                            throw new IllegalArgumentException( "Не вопроса к анкете " + id );
                        }catch( IOException e ){
                            e.printStackTrace();
                        }
                    } );
            Files.list( getAnswersDirectory() )
                    .filter( path -> !path.equals( Paths.get( "/survey/answer/.DS_Store" ) ) )
                    .filter( path -> {
                        String str = path.toString().substring( getAnswersDirectoryNameLength() );
                        return str.substring( str.lastIndexOf( "_" ) + 1 ).equals( id.toString() );
                    } )
                    .forEach( path -> {
                        try{
                            Files.delete( path );
                        }catch( FileNotFoundException e ){
                            throw new IllegalArgumentException( "Не ответа к анкете " + id );
                        }catch( IOException e ){
                            e.printStackTrace();
                        }
                    } );
        }catch( NoSuchFileException e ){
            throw new IllegalArgumentException( "Анкета " + id + " не содержится с базе" );
        }
    }

    public void saveCategory( BDCategory bdCategory ) throws IOException{
        try{
            Files.write( Paths.get( getCategoriesDirectory() + "/" + bdCategory.getName() ),
                         objectMapper.writeValueAsString( bdCategory ).getBytes( "utf-8" ),
                         StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE );
        }catch( FileAlreadyExistsException e ){
            throw new IllegalArgumentException( "Категория " + bdCategory.getName() + " уже содержится в базе" );
        }
    }

    public List<Category> listCategories( DeserializeCategoryOptions... options ) throws IOException{
        return Files.list( getCategoriesDirectory() )
                .filter( path -> !path.equals( Paths.get( getCategoriesDirectory() + "/.DS_Store" ) ) )
                .map( mapPathToCategory( options ) )
                .collect( Collectors.toList() );
    }

    public Optional<Category> findCategory( String name , DeserializeCategoryOptions...options ) throws IOException{
        return Files.list( getCategoriesDirectory() )
                .filter( path -> path.toString().substring( getCategoriesDirectoryNameLength() ).equals( name ) )
                .map( mapPathToCategory( options ) )
                .findFirst();
    }

    private Function<Path,Category> mapPathToCategory( DeserializeCategoryOptions...options ){
        return path -> {
            List<DeserializeCategoryOptions> options1 = Arrays.asList( options );
            try{
                BDCategory bdCategory = objectMapper
                        .readValue( new String( Files.readAllBytes( path ) , Charset.forName( "utf-8" ) ) , BDCategory.class );
                if( options1.contains( DeserializeCategoryOptions.SURVEYS ) )
                    bdCategory.setSurveys( listSurveysByPredicate( path1 -> {
                        String str = path1.toString().substring( getCategoriesDirectoryNameLength() );
                        return str.substring( str.lastIndexOf( "_" ) + 1 ).equals( bdCategory.getName() );
                    } , SurveysSort.TIME , Integer.MAX_VALUE ) );
                return bdCategory.toCategory();
            }catch( IOException e ){
                throw new IllegalArgumentException( "Ошибка чтения" );
            }
        };
    }

    private Boolean isCategoryNew( Category category ) throws IOException{
        return Files.list( getCategoriesDirectory() ).map(
                path -> path.toString().substring( getCategoriesDirectoryNameLength() ) ).noneMatch(
                s -> s.equals( category.getName() ) );
    }

    private List<BDSurvey> listByPredicate( Predicate<Path> predicate ) throws IOException{
        return Files.list( getSurveyDirectory() )
                .filter( predicate )
                .filter( path -> !path.equals( Paths.get( "/survey/survey/.DS_Store" ) ) )
                .map( path -> {
                    try{
                        return objectMapper.readValue( new String( Files.readAllBytes( path ), Charset.forName( "utf-8" ) ),
                                                       BDSurvey.class );
                    }catch( IOException e ){
                        throw new IllegalArgumentException( "Ошибка чтения" );
                    }
                } )
                .collect( Collectors.toList() );
    }

    private void saveDBSurvey( BDSurvey bdSurvey ) throws IOException{
        try{
            Files.write( Paths.get(
                    getSurveyDirectory().toString() + "/" + bdSurvey.getId() + "_" + bdSurvey.getCreator() + "_" +
                    bdSurvey.getCategoryName() ),
                         objectMapper.writeValueAsString( bdSurvey ).getBytes( Charset.forName( "utf-8" ) ),
                         StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE );
        }catch( FileAlreadyExistsException e ){
            throw new IllegalArgumentException( "Анкета " + bdSurvey.getId() + " уже содержится в базе" );
        }
    }

    private Path getSurveyDirectory(){
        return Paths.get( environment.getProperty( "objectsStorage" ) + environment.getProperty( "surveyStorage" ) );
    }

    private Integer getSurveyDirectoryNameLength(){
        return getSurveyDirectory().toString().length() + 1;
    }

    private Path getQuestionsDirectory(){
        return Paths.get( environment.getProperty( "objectsStorage" ) + environment.getProperty( "questionStorage" ) );
    }

    private Integer getQuestionsDirectoryNameLength(){
        return getQuestionsDirectory().toString().length() + 1;
    }

    private Path getAnswersDirectory(){
        return Paths.get( environment.getProperty( "objectsStorage" ) + environment.getProperty( "answerStorage" ) );
    }

    private Integer getAnswersDirectoryNameLength(){
        return getAnswersDirectory().toString().length() + 1;
    }

    private Path getCategoriesDirectory(){
        return Paths.get( environment.getProperty( "objectsStorage" ) + environment.getProperty( "topicStorage" ) );
    }

    private Integer getCategoriesDirectoryNameLength(){
        return getCategoriesDirectory().toString().length() + 1;
    }
}
