package ru.ssau.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssau.domain.Topic;

public interface TopicRepository extends JpaRepository<Topic, String>{
}
