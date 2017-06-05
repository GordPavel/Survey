package ru.ssau.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ssau.domain.Question;
import ru.ssau.domain.Survey;

import java.util.List;

public interface SurveyRepository extends JpaRepository<Survey, Integer>{

    @Query( "select t from Question t where t.survey.id = :id" )
    List<Question> getQuestionsBySurveyId( @Param( "id" ) Integer id );

    @Query( "select t from Survey t order by t.date desc " )
    List<Survey> getAllSortedByTime();

    @Query( "delete from Survey t where t.id = :id" )
    void delete( @Param( "id" ) Integer id );
}
