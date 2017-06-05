package ru.ssau.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ssau.domain.UserAnswer;
import ru.ssau.domain.UserAnswerPK;

import java.util.List;

public interface UserAnswersRepository extends JpaRepository<UserAnswer, UserAnswerPK>{

    @Query( "select t from UserAnswer t where t.pk.survey.id = :id" )
    List<UserAnswer> getAllAnswersOnSurveyBySurveyId( @Param( "id" ) Integer id );

    @Query( "select t from UserAnswer t where t.pk.user.login = :login" )
    List<UserAnswer> getAllAnswersOnSurveysByUserLogin( @Param( "login" ) String login );
}
