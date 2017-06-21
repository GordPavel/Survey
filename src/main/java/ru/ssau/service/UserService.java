package ru.ssau.service;

import ru.ssau.DAO.enums.DeserializeUserOptions;
import ru.ssau.domain.Survey;
import ru.ssau.domain.User;
import ru.ssau.domain.UserAnswer;
import ru.ssau.controller.UserRegistrationForm;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService{

    Optional<User> getUser( String login, DeserializeUserOptions... options ) throws InterruptedException;

    void deleteUser( String login ) throws InterruptedException;

    List<User> getUsers( DeserializeUserOptions... options ) throws InterruptedException;

    void saveUser( UserRegistrationForm user ) throws IOException, InterruptedException;

    void saveUser( User user ) throws IOException, InterruptedException;

    List<Survey> getDoneSurveysByLogin( String login ) throws InterruptedException;

    List<Survey> getMadeSurveysByLogin( String login ) throws InterruptedException;

    void saveNewUserAnswer( UserAnswer userAnswer ) throws IOException, InterruptedException;

    void deleteUserAnswer( Integer id, String login  ) throws IOException, InterruptedException;
}
