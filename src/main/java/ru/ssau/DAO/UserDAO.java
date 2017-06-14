package ru.ssau.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import ru.ssau.DAO.survey.DeserializeSurveyOption;
import ru.ssau.domain.User;

import java.util.concurrent.Semaphore;

public class UserDAO{
    private Semaphore semaphore;

    @Autowired
    private Environment environment;

    public UserDAO( Semaphore semaphore ){
        this.semaphore = semaphore;
    }

    public void beginTransaction(){
        try{
            semaphore.acquire();
        }catch( InterruptedException e ){
            e.printStackTrace();
        }
    }

    public void endTransaction(){
        semaphore.release();
    }

    public User findByLogin( String login, DeserializeSurveyOption... options ){
        return null;
    }

    public void saveNewUser( User user ){

    }

    public void updateUser( User user ){

    }


    public void deleteUser( String login ){

    }
}
