package ru.ssau.DAO;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import ru.ssau.DAO.enums.DeserializeSurveyOptions;
import ru.ssau.DAO.enums.DeserializeUserOptions;
import ru.ssau.DAO.enums.SurveysSort;
import ru.ssau.domain.*;
import ru.ssau.exceptions.*;

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
public class DAO{
    private final Semaphore    semaphore;
    private final Environment  environment;
    private final ObjectMapper objectMapper;

    @Autowired
    public DAO( Semaphore semaphore, Environment environment, ObjectMapper objectMapper){
        this.semaphore = semaphore;
        this.environment = environment;
        this.objectMapper = objectMapper;
    }

    public void beginTransaction() throws InterruptedException{
        semaphore.acquire();
    }

    public void endTransaction(){
        semaphore.release();
    }

    public List<Survey> listSurveysByPredicate( Predicate<Path> predicate, SurveysSort sort, Integer limit, Boolean downloadAnswers ,
                                                DeserializeSurveyOptions... surveyOptions ) throws IOException{
        List<DeserializeSurveyOptions> surveyOptions1 = new ArrayList<>( Arrays.asList( surveyOptions ) );
        if( sort == SurveysSort.USERS && !surveyOptions1.contains( DeserializeSurveyOptions.USERS ) ){
            surveyOptions1.add( DeserializeSurveyOptions.QUESTIONS );
            surveyOptions1.add( DeserializeSurveyOptions.USERS );
        }
        List<BDSurvey> bdSurveys = listByPredicate( predicate );
        if( surveyOptions1.contains( DeserializeSurveyOptions.CREATOR ) )
            bdSurveys.forEach( bdSurvey -> {
                try{
                    bdSurvey.setUserCreator( findUser( bdSurvey.getCreator() ).orElseThrow(
                            () -> new UserNotFoundException( bdSurvey.getCreator() ) ) );
                }catch( IOException e ){
                    throw new IllegalArgumentException( "Не удалось загразить пользователя " + bdSurvey.getCreator() );
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
                                                                 .filter( path -> !path.toString().substring( getAnswersDirectoryNameLength() ).startsWith( "." ) )
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
        if( surveyOptions1.contains( DeserializeSurveyOptions.USERS ) && surveyOptions1.contains( DeserializeSurveyOptions.QUESTIONS ) )
            bdSurveys.forEach( bdSurvey -> {
                try{
                    bdSurvey.setAnswers( listAllUserAnswersBySurveyId( bdSurvey.getId() , true ) );
                    for( int i = 0 , j , end = bdSurvey.getAnswers().size() , endJ = bdSurvey.getQuestions().size() ; i < end ; i++ )
                        for( j = 0 ; j < endJ ; j++ ){
                            Answer answer = bdSurvey.getQuestions()
                                    .get( j ).getAnswers().get( bdSurvey.getAnswers().get( i ).getAnswers().get( j ) );
                            answer.setUsersAnswered( answer.getUsersAnswered() + 1 );
                        }
                }catch( IOException e ){
                    throw new IllegalArgumentException( "Ошибка чтения" );
                }
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
        Comparator<Survey> comparator;
        if( sort == SurveysSort.TIME ) comparator = ( o1, o2 ) -> o2.getDate().compareTo( o1.getDate() );
        else comparator = ( o1, o2 ) -> Integer.compare( o2.getUsersDone(), o1.getUsersDone() );
        return bdSurveys.stream().map( BDSurvey::toSurvey ).sorted( comparator ).limit( limit ).collect(
                Collectors.toList() );
    }

    public Optional<Survey> findSurvey( Integer id, Boolean downloadAnswers , DeserializeSurveyOptions ... surveyOptions ) throws IOException{
        return listSurveysByPredicate( path -> {
            String str = path.toString().substring( getAnswersDirectoryNameLength() );
            return str.substring( 0 , str.indexOf( "_" ) ).equals( id.toString() );
        }, SurveysSort.TIME, 1 , downloadAnswers , surveyOptions ).stream().findFirst();
    }

    public Integer saveNewSurvey( Survey survey ) throws IOException{
        if( survey.getCreator() == null || survey.getCategory() == null )
            throw new IllegalArgumentException( "Нельзя сохранить такую анкету" );
        survey.setDate( new Date() );
        findUser( survey.getCreator().getLogin() ).orElseThrow( () -> new IllegalArgumentException( "Анкета без создателя" ) );
        Integer id = Files
                             .list( getSurveyDirectory() )
                             .filter( path -> ! path.toString().substring( getSurveyDirectoryNameLength() ).startsWith( "." ) )
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
            deleteUserAnswers( id );
            Files.delete( Files.list( getSurveyDirectory() )
                                  .filter( path -> {
                                      String str = path.toString().substring( getAnswersDirectoryNameLength() );
                                      return str.substring( 0 , str.indexOf( "_" ) ).equals( id.toString() );
                                  } )
                                  .findFirst()
                                  .orElseThrow( () -> new SurveyNotFoundException( id ) ) );
            Files.list( getQuestionsDirectory() )
                    .filter( path -> !path.toString().substring( getQuestionsDirectoryNameLength() ).startsWith( "." ) )
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
                    .filter( path -> !path.toString().substring( getAnswersDirectoryNameLength() ).startsWith( "." ) )
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

    public List<Category> listCategories( Boolean downloadSurveys , SurveysSort surveysSort , Integer limit ) throws IOException{
        return Files.list( getCategoriesDirectory() )
                .filter( path -> !path.toString().substring( getCategoriesDirectoryNameLength() ).startsWith( "." ) )
                .map( mapPathToCategory( downloadSurveys , surveysSort , limit ) )
                .map( category -> new CategoryRating( category ) )
                .sorted( CategoryRating.getComparator( surveysSort ) )
                .map( categoryRating -> categoryRating.toCategory() )
                .collect( Collectors.toList() );
    }

    public Optional<Category> findCategory( String name , Boolean downloadSurveys , SurveysSort surveysSort , Integer limit ) throws IOException{
        return Files.list( getCategoriesDirectory() )
                .filter( path -> path.toString().substring( getCategoriesDirectoryNameLength() ).equals( name ) )
                .map( mapPathToCategory( downloadSurveys , surveysSort , limit ) )
                .findFirst();
    }


    public List<User> listAllUsers( DeserializeUserOptions... options ) throws IOException{
        return Files.list( getUserDirectory() )
                .filter( path -> !path.toString().substring( getUserDirectoryNameLength() ).startsWith( "." ) )
                .map( mapPathToUser( options ) )
                .collect( Collectors.toList() );
    }

    public Optional<User> findUser( String login , DeserializeUserOptions...options ) throws IOException{
        return Files.list( getUserDirectory() )
                .filter( path -> path.toString().substring( getUserDirectoryNameLength() ).equals( login ) )
                .map( mapPathToUser( options ) )
                .findFirst();
    }

    public void saveUser( User user ) throws IOException{
        saveBDUser( new BDUser( user ) );
    }

    public void updateUser( String login , Consumer<BDUser> consumer ) throws IOException{
        BDUser bdUser = Files.list( getUserDirectory() )
                .filter( path -> path.toString().substring( getUserDirectoryNameLength() ).equals( login ) )
                .map( path -> {
                    try{
                        return objectMapper.readValue(
                                new String( Files.readAllBytes( path ), Charset.forName( "utf-8" ) ), BDUser.class );
                    }catch( IOException e ){
                        throw new IllegalArgumentException( "Ошибка чтения" );
                    }
                } )
                .findFirst()
                .orElseThrow( () -> new UserNotFoundException( login ) );
        consumer.accept( bdUser );
        deleteUser( bdUser.getLogin() );
        saveBDUser( bdUser );
    }

    public void deleteUser( String login ) throws IOException{
        User user = findUser( login , DeserializeUserOptions.MADESURVEYS )
                .orElseThrow( () -> new UserNotFoundException( login ) );
        Files.list( getUserAnswerDirectory() ).filter( path -> {
            String str = path.toString().substring( getUserAnswerDirectoryNameLength() );
            return str.substring( 0 , str.indexOf( "_" ) ).equals( login );
        } )
                .forEach( path -> {
                    try{
                        Files.delete( path );
                    }catch( IOException e ){
                        e.printStackTrace();
                    }
                } );
        user.getMadeSurveys().forEach( survey -> {
            try{
                deleteSurvey( survey.getId() );
            }catch( IOException e ){
                throw new IllegalArgumentException( "Не удалось удалить анкету " + survey.getId() );
            }
        } );
        Files.delete( Files.list( getUserDirectory() )
                              .filter( path -> path.toString().substring( getUserDirectoryNameLength() ).equals( login ) )
                              .findFirst()
                              .orElseThrow( () -> new UserNotFoundException( login ) ) );
    }

    private void saveBDUser( BDUser bdUser ) throws IOException{
        try{
            Files.write( Paths.get( getUserDirectory() + "/" + bdUser.getLogin() ),
                         objectMapper.writeValueAsString( bdUser ).getBytes( Charset.forName( "utf-8" ) ) ,
                         StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE );
        }catch( FileAlreadyExistsException e ){
            throw new IllegalArgumentException( "Пользователь " + bdUser.getLogin() + " уже содержится в базе" );
        }
    }


    public void saveNewUserAnswer( UserAnswer userAnswer ) throws IOException{
        findUser( userAnswer.getUser().getLogin() ).orElseThrow( () -> new IllegalArgumentException( "Нет такого пользователя " + userAnswer.getUser().getLogin() ) );
        Survey survey = findSurvey( userAnswer.getSurvey().getId() , false , DeserializeSurveyOptions.QUESTIONS ).orElseThrow( () -> new IllegalArgumentException( "Нет такой анкеты " + userAnswer.getSurvey().getId() ) );
        if( survey.getQuestions().size() != userAnswer.getAnswers().size() )
            throw new IllegalArgumentException( "Несоответствие ответа анкете" );
        for( int i = 0 , end = survey.getQuestions().size() ; i < end ; i++ )
            if( survey.getQuestions().get( i ).getAnswers().size() <= userAnswer.getAnswers().get( i ) )
                throw new IllegalArgumentException( "Несоответствие ответа анкете" );

        try{
            Files.write( Paths.get( getUserAnswerDirectory() + "/" + userAnswer.getUser().getLogin() + "_" + userAnswer.getSurvey().getId() ) ,
                         objectMapper.writeValueAsString( userAnswer.getAnswers() ).getBytes( Charset.forName( "utf-8" ) ) ,
                         StandardOpenOption.CREATE_NEW , StandardOpenOption.WRITE );
        }catch( FileAlreadyExistsException e ){
            throw new SurveyAlreadyDoneByUserException( userAnswer.getUser().getLogin() , userAnswer.getSurvey().getId() );
        }
    }

    public List<UserAnswer> listAllUserAnswersByUserLogin( String login , Boolean downloadAnswers ) throws IOException{
        return listAllUserAnswersByPredicate( path -> {
            String str = path.toString().substring( getUserAnswerDirectoryNameLength() );
            return str.substring( 0 , str.indexOf( "_" ) ).equals( login );
        } , downloadAnswers );
    }

    public List<UserAnswer> listAllUserAnswersBySurveyId( Integer id , Boolean downloadAnswers ) throws IOException{
        return listAllUserAnswersByPredicate( path -> {
            String str = path.toString().substring( getUserAnswerDirectoryNameLength() );
            return str.substring( str.indexOf( "_" ) + 1 ).equals( id.toString() );
        } , downloadAnswers );
    }

    public void deleteUserAnswers( Integer surveyId ) throws IOException{
        Files.list( getUserAnswerDirectory() ).filter( path -> {
            String str = path.toString().substring( getUserAnswerDirectoryNameLength() );
            return str.substring( str.indexOf( "_" ) + 1 ).equals( surveyId.toString() );
        } )
                              .forEach( path -> {
                                  try{
                                      Files.delete( path );
                                  }catch( IOException e ){
                                      e.printStackTrace();
                                  }
                              } );
    }

    public void deleteUserAnswer( Integer id, String login ) throws IOException{
        Files.delete( Files.list( getUserAnswerDirectory() )
                              .filter( path -> path.toString().substring( getUserAnswerDirectoryNameLength() ).equals( login + "_" + id ) )
                              .findFirst().orElseThrow( () -> new UserAnswerNotFoundException( login, id ) ) );
    }

    private List<UserAnswer> listAllUserAnswersByPredicate( Predicate<Path> predicate , Boolean downloadAnswers )
            throws IOException{
        return Files.list( getUserAnswerDirectory() )
                .filter( predicate )
                .map( path -> {
                    BDUserAnswer bdUserAnswer = new BDUserAnswer();
                    String str = path.toString().substring( getUserAnswerDirectoryNameLength() );
                    bdUserAnswer.setUserLogin( str.substring( 0 , str.indexOf( "_" ) ) );
                    bdUserAnswer.setSurveyId( Integer.parseInt( str.substring( str.indexOf( "_" ) + 1 ) ) );
                    try{
                        bdUserAnswer.setUser( findUser( bdUserAnswer.getUserLogin() ).orElseThrow( () -> new UserNotFoundException( bdUserAnswer.getUserLogin() ) ) );
                        bdUserAnswer.setSurvey( findSurvey( bdUserAnswer.getSurveyId() , false ).orElseThrow( () -> new SurveyNotFoundException( bdUserAnswer.getSurveyId() ) ) );
                    }catch( IOException e ){
                        e.printStackTrace();
                    }
                    if( downloadAnswers ){
                        try{
                            bdUserAnswer.setAnswersList( new ObjectMapper().readValue( new String( Files.readAllBytes( path ), Charset.forName( "utf-8" ) ) , new TypeReference<List<Integer>>(){} ) );
                        }catch( FileNotFoundException e ){
                            throw new IllegalArgumentException( "Нет в базе " + path );
                        }catch( IOException e ){
                            throw new IllegalArgumentException( "Ошибка чтения" );
                        }
                    }
                    return bdUserAnswer.toUserAnswer();
                } )
                .collect( Collectors.toList() );
    }



    private Function<Path,Category> mapPathToCategory( Boolean downloadSurveys , SurveysSort surveysSort , Integer limit ){
        return path -> {
            try{
                BDCategory bdCategory = objectMapper
                        .readValue( new String( Files.readAllBytes( path ) , Charset.forName( "utf-8" ) ) , BDCategory.class );
                if( downloadSurveys ){
                    Boolean downloadAnswers = surveysSort != SurveysSort.TIME;
                    bdCategory.setSurveys( listSurveysByPredicate( path1 -> {
                        String str = path1.toString().substring( getCategoriesDirectoryNameLength() );
                        return str.substring( str.lastIndexOf( "_" ) + 1 ).equals( bdCategory.getName() );
                    }, surveysSort, limit, downloadAnswers ) );
                }
                return bdCategory.toCategory();
            }catch( IOException e ){
                throw new IllegalArgumentException( "Ошибка чтения" );
            }
        };
    }

    private Function<Path,User> mapPathToUser( DeserializeUserOptions...options ){
        return path -> {
            try{
                BDUser bdUser = objectMapper.readValue( new String( Files.readAllBytes( path ), Charset.forName( "utf-8" ) ), BDUser.class );
                List<DeserializeUserOptions> options1 = Arrays.asList( options );
                if( options1.contains( DeserializeUserOptions.MADESURVEYS ) ) bdUser.setMadeByUserSurveys( listSurveysByPredicate( path1 -> {
                    String str = path1.toString().substring( getSurveyDirectoryNameLength() );
                    return str.substring( str.indexOf( "_" ) + 1, str.lastIndexOf( "_" ) ).equals( bdUser.getLogin() );
                }, SurveysSort.TIME, Integer.MAX_VALUE , false ) );
                if( options1.contains( DeserializeUserOptions.ANSWERS ) )
                    bdUser.setAnswers( listAllUserAnswersByUserLogin( bdUser.getLogin() , true ) );
                return bdUser.toUser();
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
                .filter( path -> !path.toString().substring( getSurveyDirectoryNameLength() ).startsWith( "." ) )
                .filter( predicate )
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

    private Path getUserDirectory(){
        return Paths.get( environment.getProperty( "objectsStorage" ) + environment.getProperty( "userStorage" ) );
    }

    private Integer getUserDirectoryNameLength(){
        return getUserDirectory().toString().length() + 1;
    }

    private Path getUserAnswerDirectory(){
        return Paths.get( environment.getProperty( "objectsStorage" ) + environment.getProperty( "userAnswerStorage" ) );
    }

    private Integer getUserAnswerDirectoryNameLength(){
        return getUserAnswerDirectory().toString().length() + 1;
    }
}

class CategoryRating {
    private Category category;
    private Date     date;
    private Integer  usersAnswered;

    CategoryRating( Category category ){
        this.category = category;
        if( category.getSurveys() !=  null ){
            this.date = category.getSurveys().stream().map( Survey::getDate ).max( Comparator.reverseOrder() ).get();
            if( category.getSurveys().get( 0 ).getQuestions() != null )
                this.usersAnswered = category.getSurveys().stream().mapToInt( Survey::getUsersDone ).sum();
        }
    }

    Category toCategory(){
        return this.category;
    }

    static Comparator<CategoryRating> getComparator( SurveysSort sort ){
        if( sort == SurveysSort.USERS )
            return ( o1, o2 ) -> Integer.compare( o2.usersAnswered , o1.usersAnswered );
        else return ( o1, o2 ) -> o2.date.compareTo( o1.date );
    }
}
