package ru.ssau.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.domain.Survey;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Transactional
@Repository
public class SurveyDAOImpl implements SurveyDAO{

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Override
    public Survey createNewSurvey( Survey survey ){
        return null;
    }

    @Override
    public void removeSurvey( Survey survey ){

    }

    @Override
    public void updateSurvey( Survey survey ){

    }

    @Override
    public Survey getById( Integer integer ){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Survey survey = entityManager.find( Survey.class, 1 );
        entityManager.getTransaction().commit();
        entityManager.close();
        return survey;
    }
}
