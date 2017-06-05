package ru.ssau.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ssau.domain.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Integer>{

    @Query( "delete from Answer t where t.id = :id" )
    void deleteById( @Param( "id" ) Integer id );
}
