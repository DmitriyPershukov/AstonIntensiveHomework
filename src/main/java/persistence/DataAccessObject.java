package persistence;

import java.util.List;
import java.util.Optional;

public interface DataAccessObject<T> {
    Optional<T> findById(Long id);

    List<T> findAll();

    void save(T entity);

    void update(T entity);

    void deleteById(Long id);

    boolean existsById(Long id);
}
