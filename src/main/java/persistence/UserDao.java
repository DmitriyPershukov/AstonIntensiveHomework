package persistence;

import model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findById(Long id);

    List<User> findAll();

    void save(User user);

    void update(User user);

    void deleteById(Long id);

    boolean existsById(Long id);
}
