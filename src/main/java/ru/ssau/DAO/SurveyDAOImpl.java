package ru.ssau.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.domain.Survey;

@Transactional
@Repository
public class SurveyDAOImpl implements SurveyDAO{

    @Autowired
    private SessionFactory sessionFactory;

    protected Session getSession(){
        return this.sessionFactory.getCurrentSession();
    }

    @Override
    public void createNewSurvey( Survey survey ){
        getSession().save( survey );
    }

    @Override
    public void removeSurvey( Survey survey ){

    }

    @Override
    public void updateSurvey( Survey survey ){

    }

    @Override
    public Survey getById( Integer id ){
        return getSession().find( Survey.class, id );
    }
}
