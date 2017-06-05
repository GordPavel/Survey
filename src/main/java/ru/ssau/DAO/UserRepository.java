package ru.ssau.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ssau.domain.User;

public interface UserRepository extends JpaRepository<User, String>{
}
