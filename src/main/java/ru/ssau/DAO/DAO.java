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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private Stream<Survey> listAllSurveys( Predicate<Path> predicate , SurveysSort surveysSort , Integer limit , Boolean downloadQuestions, Boolean downloadCreator, Boolean downloadUsers,
                                           Boolean downloadStatistics, Boolean downloadCategory ) throws IOException{
        Comparator<Survey> surveyComparator;
        if( surveysSort == SurveysSort.USERS ){
            downloadUsers = true;
            surveyComparator = ( o1, o2 ) -> Integer.compare( o2.getUsersDone() , o1.getUsersDone() );
        }else
            surveyComparator = ( o1, o2 ) -> o2.getDate().compareTo( o1.getDate() );
        return Files.list( getSurveyDirectory() )
                .filter( path -> ! path.toString().substring( getSurveyDirectoryNameLength() ).startsWith( "." ) )
                .filter( predicate )
                .limit( limit )
                .map( mapPathToSurvey( downloadQuestions , downloadCreator , downloadUsers , downloadStatistics , downloadCategory ) )
                .sorted( surveyComparator );
    }

    public List<Survey> listAllSurveys( SurveysSort surveysSort , Integer limit , DeserializeSurveyOptions... options ) throws IOException{
        List<DeserializeSurveyOptions> surveyOptions = new ArrayList<>( Arrays.asList( options ) );
        if( surveysSort == SurveysSort.USERS )
            surveyOptions.add( DeserializeSurveyOptions.USERS );
        Boolean downloadUsers = surveyOptions.contains( DeserializeSurveyOptions.USERS );
        return listAllSurveys( path -> true , surveysSort , limit , false , false, downloadUsers , false , false )
                .collect( Collectors.toList());
    }

    public Optional<Survey> findSurvey( Integer id , DeserializeSurveyOptions... options ) throws IOException{
        List<DeserializeSurveyOptions> surveyOptions = Arrays.asList( options );
        Boolean downloadQuestions = surveyOptions.contains( DeserializeSurveyOptions.QUESTIONS ),
                downloadCreator = surveyOptions.contains( DeserializeSurveyOptions.CREATOR ),
                downloadUsers = surveyOptions.contains( DeserializeSurveyOptions.USERS ) || surveyOptions.contains( DeserializeSurveyOptions.STATISTICS ),
                downloadStatistics = surveyOptions.contains( DeserializeSurveyOptions.STATISTICS ),
                downloadCategory = surveyOptions.contains( DeserializeSurveyOptions.CATEGORY );
        return listAllSurveys( path -> {
            String str = path.toString().substring( getSurveyDirectoryNameLength() );
            return str.substring( 0 , str.indexOf( "_" ) ).equals( id.toString() );
        } , SurveysSort.TIME , 1 , downloadQuestions , downloadCreator , downloadUsers , downloadStatistics , downloadCategory ).findFirst();
    }

    public Integer saveNewSurvey( Survey survey ) throws IOException{
        Integer id = Files.list( getSurveyDirectory() )
                .filter( path -> ! path.toString().substring( getSurveyDirectoryNameLength() ).startsWith( "." ) )
                .mapToInt( path -> {
                    String str = path.toString().substring( getSurveyDirectoryNameLength() );
                    return Integer.parseInt( str.substring( 0 , str.indexOf( "_" ) ) );
                } )
                .max()
                .orElse( -1 ) + 1;
        survey.setId( id );
        survey.setDate( new Date() );
        if( survey.getCreator() == null )
            throw new SaveSurveyException( "creator" );
        if( ! findUser( survey.getCreator().getLogin() ).isPresent() )
            throw new SaveSurveyException( "exists in database creator" );
        if( survey.getCategory() == null )
            throw new SaveSurveyException( "category" );
        Files.write( Paths.get( getSurveyDirectory() + "/" + survey.getId() + "_" + survey.getCreator().getLogin() + "_" + survey.getCategory().getName() ) ,
                     objectMapper.writeValueAsString( new BDSurvey( survey ) ).getBytes( Charset.forName( "utf-8" ) ) ,
                     StandardOpenOption.CREATE_NEW );
        survey.getQuestions().stream()
                .peek( new Consumer<Question>(){
                    int i = 0;
                    @Override
                    public void accept( Question question ){
                        question.setId( i++ );
                        question.getAnswers().forEach( new Consumer<Answer>(){
                            int j = 0;
                            @Override
                            public void accept( Answer answer ){
                                answer.setId( j++ );
                            }
                        } );
                    }
                } )
                .peek( question -> question.getAnswers().stream().map( BDAnswer::new )
                .forEach( bdAnswer -> {
                    Path path = Paths.get( getAnswersDirectory() + "/" + bdAnswer.getId() + "_" + question.getId() + "_" + survey.getId() );
                    try{
                        Files.write( path , objectMapper.writeValueAsString( bdAnswer ).getBytes( Charset.forName( "utf-8" ) ) , StandardOpenOption.CREATE_NEW );
                    }catch( IOException e ){
                        e.printStackTrace();
                        throw new SerializationException( path );
                    }
                } ) )
                .map( BDQuestion::new )
                .forEach( bdQuestion -> {
                    Path path = Paths.get( getQuestionsDirectory() + "/" + bdQuestion.getId() + "_" + survey.getId() );
                    try{
                        Files.write( path , objectMapper.writeValueAsString( bdQuestion ).getBytes( Charset.forName( "utf-8" ) ) , StandardOpenOption.CREATE_NEW );
                    }catch( IOException e ){
                        e.printStackTrace();
                        throw new SerializationException( path );
                    }
                } );
        if( isCategoryNew( survey.getCategory() ) )
            saveCategory( survey.getCategory() );
        return id;
    }

    public void deleteSurvey( Integer id ) throws IOException{
        Files.delete( Files.list( getSurveyDirectory() )
                              .filter(
                                path1 -> !path1.toString().substring( getSurveyDirectoryNameLength() ).startsWith( "." ) )
                                              .filter(
                                path -> {
                                    String str = path.toString().substring( getSurveyDirectoryNameLength() );
                                    return str.substring( 0, str.indexOf( "_" ) ).equals( id.toString() );
                                } )
                              .findFirst()
                              .orElseThrow( () -> new SurveyNotFoundException( id ) ) );
        Files.list( getQuestionsDirectory() )
                .filter( path -> ! path.toString().substring( getQuestionsDirectoryNameLength() ).startsWith( "." ) )
                .filter( path -> {
                    String str = path.toString().substring( getQuestionsDirectoryNameLength() );
                    return str.substring( str.indexOf( "_" ) + 1 ).equals( id.toString() );
                } )
                .forEach( path -> {
                    try{
                        Files.delete( path );
                    }catch( IOException e ){
                        throw new DeserializationException( path );
                    }
                } );
        Files.list( getAnswersDirectory() )
                .filter( path -> ! path.toString().substring( getAnswersDirectoryNameLength() ).startsWith( "." ) )
                .filter( path -> {
                    String str = path.toString().substring( getAnswersDirectoryNameLength() );
                    return str.substring( str.lastIndexOf( "_" ) + 1 ).equals( id.toString() );
                } )
                .forEach( path -> {
                    try{
                        Files.delete( path );
                    }catch( IOException e ){
                        throw new DeserializationException( path );
                    }
                } );
        Files.list( getUserAnswerDirectory() )
                .filter( path -> ! path.toString().substring( getUserAnswerDirectoryNameLength() ).startsWith( "." ) )
                .filter( path -> {
                    String str = path.toString().substring( getUserAnswerDirectoryNameLength() );
                    return str.substring( str.lastIndexOf( "_" ) + 1 ).equals( id.toString() );
                } )
                .forEach( path -> {
                    try{
                        Files.delete( path );
                    }catch( IOException e ){
                        throw new DeserializationException( path );
                    }
                } );
    }



    private Stream<Category> listAllCategories( Predicate<Path> predicate , Boolean downloadSurveys , SurveysSort surveysSort , Integer limit )
            throws IOException{
        Comparator<Category> comparator = Comparator.comparing( Category::getName );
        if( downloadSurveys ){
            if( surveysSort == SurveysSort.USERS ){
                comparator = Comparator.comparing( category -> category.getSurveys().stream().mapToInt( Survey::getUsersDone ).max().orElse( 0 ) );
            }else{
                Date date = new Date();
                date.setYear( 1970 );
                comparator = Comparator.comparing( category -> category.getSurveys().stream().map( Survey::getDate ).max( Comparator.comparing( Date::getTime ) ).orElse( date ) );
            }
        }
        return Files.list( getCategoriesDirectory() )
                .filter( path -> ! path.toString().substring( getCategoriesDirectoryNameLength() ).startsWith( "." ) )
                .filter( predicate )
                .map( mapPathToCategory( downloadSurveys , surveysSort , limit ) )
                .sorted( comparator );
    }

    public List<Category> listAllCategories( Boolean downloadSurveys , SurveysSort surveysSort , Integer limit )
            throws IOException{
        return listAllCategories( path -> true , downloadSurveys , surveysSort , limit ).collect( Collectors.toList());
    }

    public Optional<Category> findCategory( String name , Boolean downloadSurveys , SurveysSort surveysSort , Integer limit  ) throws IOException{
        return listAllCategories( path ->  path.toString().substring( getCategoriesDirectoryNameLength() ).equals( name ), downloadSurveys , surveysSort , limit ).findFirst();
    }

    private void saveCategory( Category category ){
        Path path = Paths.get( getCategoriesDirectory() + "/" + category.getName() );
        try{
            Files.write( path , objectMapper.writeValueAsString( new BDCategory( category ) ).getBytes( Charset.forName( "utf-8" ) ) ,
                         StandardOpenOption.CREATE_NEW );
        }catch( IOException e ){
            throw new SerializationException( path );
        }
    }



    private Stream<User> listAllUsers( Predicate<Path> predicate , DeserializeUserOptions...options ) throws IOException{
        List<DeserializeUserOptions> userOptions = Arrays.asList( options );
        Boolean downloadMadeSurveys = userOptions.contains( DeserializeUserOptions.MADESURVEYS ),
                downloadDoneSurveys = userOptions.contains( DeserializeUserOptions.ANSWERS );
        return Files.list( getUserDirectory() )
                .filter( path -> ! path.toString().substring( getUserDirectoryNameLength() ).startsWith( "." ) )
                .filter( predicate )
                .map( mapPathToUser( downloadMadeSurveys , downloadDoneSurveys ) );
    }

    public List<User> listAllUsers( DeserializeUserOptions... options ) throws IOException{
        return listAllUsers( path -> true, options ).collect( Collectors.toList() );
    }

    public Optional<User> findUser( String login , DeserializeUserOptions...options ) throws IOException{
        return listAllUsers( path -> path.toString().substring( getUserDirectoryNameLength() ).equals( login ) , options ).findFirst();
    }

    public void saveUser( User user ) throws IOException{
        saveBDUser( new BDUser( user ) );
    }

    public void updateUser( String login, Consumer<BDUser> consumer ){
        try{
            BDUser bdUser = Files.list( getUserDirectory() )
                    .filter( path -> ! path.toString().substring( getUserDirectoryNameLength() ).startsWith( "." ) )
                    .filter( path -> path.toString().substring( getUserDirectoryNameLength() ).equals( login ) )
                    .map( this::mapPathToBDUser )
                    .findFirst().orElseThrow( () -> new UserNotFoundException( login ) );
            consumer.accept( bdUser );
            deleteBDUser( bdUser.getLogin() );
            saveBDUser( bdUser );
        }catch( IOException e ){
            e.printStackTrace();
            throw new DeserializationException( Paths.get( getUserDirectory() + "/" + login ) );
        }
    }

    public void deleteUser( String login ) throws IOException{
        deleteBDUser( login );
        Files.list( getSurveyDirectory() )
                .filter( path -> ! path.toString().substring( getSurveyDirectoryNameLength() ).startsWith( "." ) )
                .filter( path -> {
                    String str = path.toString().substring( getSurveyDirectoryNameLength() );
                    return str.substring( str.indexOf( "_" ) + 1 , str.lastIndexOf( "_" ) ).equals( login );
                } )
                .forEach( path -> {
                    String str = path.toString().substring( getSurveyDirectoryNameLength() );
                    try{
                        deleteSurvey( Integer.parseInt( str.substring( 0 , str.indexOf( "_" ) ) ) );
                    }catch( IOException e ){
                        e.printStackTrace();
                        throw new DeserializationException( path );
                    }
                } );
        Files.list( getUserAnswerDirectory() )
                .filter( path -> ! path.toString().substring( getUserAnswerDirectoryNameLength() ).startsWith( "." ) )
                .filter( path -> {
                    String str = path.toString().substring( getUserAnswerDirectoryNameLength() );
                    return str.substring( 0 , str.indexOf( "_" ) ).equals( login );
                } )
                .forEach( path -> {
                    try{
                        Files.delete( path );
                    }catch( IOException e ){
                        e.printStackTrace();
                        throw new DeserializationException( path );
                    }
                } );
        Files.list( getUsersImagesDirectory() )
                .filter( path -> ! path.toString().substring( getUsersImagesDirectoryNameLength() ).startsWith( "." ) )
                .filter( path -> path.toString().substring( getUsersImagesDirectoryNameLength() ).equals( login ) )
                .forEach( path -> {
                    try{
                        Files.delete( path );
                    }catch( IOException e ){
                        e.printStackTrace();
                        throw new DeserializationException( path );
                    }
                } );
    }

    private Stream<UserAnswer> getUserAnswers( Predicate<Path> predicate , Boolean downloadStatistics ) throws IOException{
        return Files.list( getUserAnswerDirectory() )
                .filter( path -> ! path.toString().substring( getUserAnswerDirectoryNameLength() ).startsWith( "." ) )
                .filter( predicate )
                .map( mapPathToUserAnswer( downloadStatistics ) );
    }

    public List<UserAnswer> listAllUserAnswersByLogin( String login , Boolean downloadStatistics ) throws IOException{
        return getUserAnswers( path -> {
            String str = path.toString().substring( getUserAnswerDirectoryNameLength() );
            return str.substring( 0 , str.indexOf( "_" ) ).equals( login );
        } , downloadStatistics ).collect( Collectors.toList() );
    }

    public List<UserAnswer> listAllUserAnswersById( Integer id , Boolean downloadStatistics ) throws IOException{
        return getUserAnswers( path -> {
            String str = path.toString().substring( getUserAnswerDirectoryNameLength() );
            return str.substring( str.indexOf( "_" ) + 1 ).equals( id.toString() );
        } , downloadStatistics ).collect( Collectors.toList() );
    }

    public void saveNewUserAnswer( UserAnswer userAnswer ) throws IOException{
        Files.write( Paths.get( getUserAnswerDirectory() + "/" + userAnswer.getUser().getLogin() + "_" + userAnswer.getSurvey().getId() ) ,
                     objectMapper.writeValueAsString( userAnswer.getAnswers() ).getBytes( Charset.forName( "utf-8" ) ) ,
                     StandardOpenOption.CREATE_NEW , StandardOpenOption.WRITE );
    }

    public void deleteUserAnswer( Integer id , String login ) throws IOException{
        Files.list( getUserAnswerDirectory() )
                .filter( path -> ! path.toString().substring( getUserAnswerDirectoryNameLength() ).startsWith( "." ) )
                .filter( path -> path.toString().substring( getUserAnswerDirectoryNameLength() ).equals( login + "_" + id ) )
                .forEach( path -> {
                    try{
                        Files.delete( path );
                    }catch( IOException e ){
                        e.printStackTrace();
                        throw new DeserializationException( path );
                    }
                } );
    }

    public Boolean isUserAnswerAlreadyExists( UserAnswer userAnswer ){
        try{
            return Files.list( getUserAnswerDirectory() )
                    .filter( path -> ! path.toString().substring( getUserAnswerDirectoryNameLength() ).startsWith( "." ) )
                    .anyMatch( path -> path.toString().substring( getUserAnswerDirectoryNameLength() ).equals( userAnswer.getUser().getLogin() + "_" + userAnswer.getSurvey().getId() ) );
        }catch( IOException e ){
            e.printStackTrace();
            return true;
        }
    }

    private Boolean isCategoryNew( Category category ){
        try{
            return Files.list( getCategoriesDirectory() )
                    .filter( path -> ! path.toString().substring( getCategoriesDirectoryNameLength() ).startsWith( "." ) )
                    .noneMatch( path ->  path.toString().substring( getCategoriesDirectoryNameLength() ).equals( category.getName() ) );
        }catch( IOException e ){
            e.printStackTrace();
            return false;
        }
    }

    private Function<Path,Survey> mapPathToSurvey( Boolean downloadQuestions, Boolean downloadCreator, Boolean downloadUsers,
                                                   Boolean downloadStatistics, Boolean downloadCategory ){
        return path -> {
            try{
                BDSurvey bdSurvey = objectMapper.readValue( new String( Files.readAllBytes( path ) , Charset.forName( "utf-8" ) ) , BDSurvey.class );
                if( downloadCreator )
                    bdSurvey.setUserCreator( findUser( bdSurvey.getCreator() ).orElseThrow( () -> new UserNotFoundException( bdSurvey.getCreator() ) ) );
                if( downloadUsers )
                    bdSurvey.setAnswers( listAllUserAnswersById( bdSurvey.getId() , downloadStatistics ) );
                if( downloadQuestions ){
                    bdSurvey.setQuestions( Files.list( getQuestionsDirectory() )
                                                   .filter( path1 -> ! path1.toString().substring( getSurveyDirectoryNameLength() ).startsWith( "." ) )
                                                   .filter( path1 -> {
                                                       String str = path1.toString().substring( getQuestionsDirectoryNameLength() );
                                                       return str.substring( str.indexOf( "_" ) + 1 ).equals( bdSurvey.getId().toString() );
                                                   } )
                                                   .map( mapPathToQuestion() )
                                                   .sorted( Comparator.comparing( Question::getId ) )
                                                   .collect( Collectors.toList() ) );
                    if( downloadStatistics )
                        for( int i = 0 , j , end = bdSurvey.getAnswers().size() , endJ = bdSurvey.getQuestions().size() ; i < end ; i++ )
                            for( j = 0 ; j < endJ; j++ )
                                bdSurvey.getQuestions().get( j ).getAnswers().get( bdSurvey.getAnswers().get( i ).getAnswers().get( j ) ).incrementUsersAnswered();
                }
                if( downloadCategory )
                    bdSurvey.setCategory( findCategory( bdSurvey.getCategoryName() , false , SurveysSort.TIME , 0 )
                                                  .orElseThrow( () -> new CategoryNotFoundException( bdSurvey.getCategoryName() ) ) );
                return bdSurvey.toSurvey();
            }catch( IOException e ){
                throw new DeserializationException( path );
            }
        };
    }

    private Function<Path,UserAnswer> mapPathToUserAnswer( Boolean downloadStatistics ){
        return path -> {
            try{
                BDUserAnswer bdUserAnswer = new BDUserAnswer();
                String str = path.toString().substring( getUserAnswerDirectoryNameLength() );
                String login = str.substring( 0 , str.indexOf( "_" ) );
                Integer id = Integer.parseInt( str.substring( str.indexOf( "_" ) + 1 ) );
                bdUserAnswer.setUser( findUser( login ).orElseThrow( () -> new UserNotFoundException( login ) ) );
                bdUserAnswer.setSurvey( findSurvey( id ).orElseThrow( () -> new SurveyNotFoundException( id ) ) );
                if( downloadStatistics )
                    try{
                        bdUserAnswer.setAnswersList( new ObjectMapper().readValue( new String( Files.readAllBytes( path ), Charset.forName( "utf-8" ) ) , new TypeReference<List<Integer>>(){} ) );
                    }catch( FileNotFoundException e ){
                        throw new IllegalArgumentException( "Нет в базе " + path );
                    }catch( IOException e ){
                        throw new DeserializationException( path );
                    }
                return bdUserAnswer.toUserAnswer();
            }catch( IOException e ){
                e.printStackTrace();
                throw new DeserializationException( path );
            }
        };
    }

    private Function<Path,Category> mapPathToCategory( Boolean downloadSurveys , SurveysSort surveysSort , Integer limit ){
        return path -> {
            try{
                BDCategory bdCategory = objectMapper.readValue( new String( Files.readAllBytes( path ) , Charset.forName( "utf-8" ) ), BDCategory.class );
                if( downloadSurveys ) bdCategory.setSurveys( listAllSurveys( path1 -> {
                    String str = path1.toString().substring( getSurveyDirectoryNameLength() );
                    return str.substring( str.lastIndexOf( "_" ) + 1 ).equals( bdCategory.getName() );
                }, surveysSort, limit, false, false, false, false, false ).collect( Collectors.toList() ) );
                return bdCategory.toCategory();
            }catch( IOException e ){
                e.printStackTrace();
                throw new DeserializationException( path );
            }
        };
    }

    private Function<Path,Question> mapPathToQuestion(){
        return  path -> {
            try{
                String survey = path.toString().substring( getQuestionsDirectoryNameLength() );
                Integer surveyId = Integer.parseInt( survey.substring( survey.indexOf( "_" ) + 1 ) );
                BDQuestion bdQuestion = objectMapper.readValue( new String( Files.readAllBytes( path ) , Charset.forName( "utf-8" ) ), BDQuestion.class );
                bdQuestion.setAnswers( Files.list( getAnswersDirectory() )
                                               .filter( path1 -> !path1.toString().substring( getAnswersDirectoryNameLength() ).startsWith( "." ) )
                                               .filter( path1 -> {
                                                   String str = path1.toString().substring( getAnswersDirectoryNameLength() );
                                                   return str.substring( str.indexOf( "_" ) + 1 ).equals( bdQuestion.getId().toString() + "_" + surveyId.toString() );
                                               } )
                                               .map( mapPathToAnswer() )
                                               .sorted( Comparator.comparing( Answer::getId ) )
                                               .collect( Collectors.toList() ) );
                return bdQuestion.toQuestion();
            }catch( IOException e ){
                e.printStackTrace();
                throw new DeserializationException( path );
            }
        };
    }

    private Function<Path,Answer> mapPathToAnswer(){
        return path -> {
            try{
                Answer answer = objectMapper.readValue( new String( Files.readAllBytes( path ), Charset.forName( "utf-8" ) ) , BDAnswer.class ).toAnswer();
                answer.setUsersAnswered( 0 );
                return answer;
            }catch( IOException e ){
                e.printStackTrace();
                throw new DeserializationException( path );
            }
        };
    }

    private Function<Path , User> mapPathToUser( Boolean downloadMadeSurveys , Boolean downloadDoneSurveys ){
        return path -> {
            BDUser bdUser = mapPathToBDUser( path );
            if( downloadMadeSurveys ) try{
                bdUser.setMadeByUserSurveys( listAllSurveys( path1 -> {
                    String str = path1.toString().substring( getSurveyDirectoryNameLength() );
                    return str.substring( str.indexOf( "_" ) + 1, str.lastIndexOf( "_" ) ).equals( bdUser.getLogin() );
                }, SurveysSort.TIME, Integer.MAX_VALUE, false, false, false, false, false )
                                                     .collect( Collectors.toList() ) );
            }catch( IOException e ){
                e.printStackTrace();
                throw new DeserializationException( path );
            }
            if( downloadDoneSurveys ) try{
                bdUser.setAnswers( listAllUserAnswersByLogin( bdUser.getLogin(), false ) );
            }catch( IOException e ){
                e.printStackTrace();
                throw new DeserializationException( path );
            }
            return bdUser.toUser();
        };
    }

    private BDUser mapPathToBDUser( Path path ){
        try{
            return objectMapper.readValue( new String( Files.readAllBytes( path ), Charset.forName( "utf-8" ) ),
                                           BDUser.class );
        }catch( IOException e ){
            e.printStackTrace();
            throw new DeserializationException( path );
        }
    }


    private void deleteBDUser( String login ) throws IOException{
        Files.delete( Files.list( getUserDirectory() )
                .filter( path -> ! path.toString().substring( getUserDirectoryNameLength() ).startsWith( "." ) )
                .filter( path -> path.toString().substring( getUserDirectoryNameLength() ).equals( login ) )
                .findFirst().orElseThrow( () -> new UserNotFoundException( login ) ) );
    }

    private void saveBDUser( BDUser bdUser ) throws IOException{
        Path path = Paths.get( getUserDirectory() + "/" + bdUser.getLogin() );
        Files.write( path , objectMapper.writeValueAsString( bdUser ).getBytes( Charset.forName( "utf-8" ) ) , StandardOpenOption.CREATE_NEW );
    }

    private Path getSurveyDirectory(){
        return Paths.get( environment.getProperty( "storage" ) + environment.getProperty( "surveyStorage" ) );
    }

    private Integer getSurveyDirectoryNameLength(){
        return getSurveyDirectory().toString().length() + 1;
    }

    private Path getQuestionsDirectory(){
        return Paths.get( environment.getProperty( "storage" ) + environment.getProperty( "questionStorage" ) );
    }

    private Integer getQuestionsDirectoryNameLength(){
        return getQuestionsDirectory().toString().length() + 1;
    }

    private Path getAnswersDirectory(){
        return Paths.get( environment.getProperty( "storage" ) + environment.getProperty( "answerStorage" ) );
    }

    private Integer getAnswersDirectoryNameLength(){
        return getAnswersDirectory().toString().length() + 1;
    }

    private Path getCategoriesDirectory(){
        return Paths.get( environment.getProperty( "storage" ) + environment.getProperty( "topicStorage" ) );
    }

    private Integer getCategoriesDirectoryNameLength(){
        return getCategoriesDirectory().toString().length() + 1;
    }

    private Path getUserDirectory(){
        return Paths.get( environment.getProperty( "storage" ) + environment.getProperty( "userStorage" ) );
    }

    private Integer getUserDirectoryNameLength(){
        return getUserDirectory().toString().length() + 1;
    }

    private Path getUserAnswerDirectory(){
        return Paths.get( environment.getProperty( "storage" ) + environment.getProperty( "userAnswerStorage" ) );
    }

    private Integer getUserAnswerDirectoryNameLength(){
        return getUserAnswerDirectory().toString().length() + 1;
    }

    private Path getUsersImagesDirectory(){
        return Paths.get( environment.getProperty( "filesStorage" ) );
    }

    private Integer getUsersImagesDirectoryNameLength(){
        return getUsersImagesDirectory().toString().length() + 1;
    }
}