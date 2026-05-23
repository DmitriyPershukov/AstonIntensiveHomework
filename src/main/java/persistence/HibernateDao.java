package persistence;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class HibernateDao<T> implements DataAccessObject<T>{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Class<T> entityClass;

    public HibernateDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    @Override
    public Optional<T> findById(Long id){
        logger.info("Fetching user with id={} from the database.", id);
        return Optional.ofNullable(SessionFactoryMaker.getFactory().fromTransaction(session -> session
                .find(entityClass, id)));
    }

    @Override
    public List<T> findAll() {
        logger.info("Fetching all users from the database.");
        return SessionFactoryMaker.getFactory().fromTransaction(session -> session
                .createSelectionQuery(String.format("from %s", entityClass.getSimpleName()), entityClass)
                .getResultList());
    }

    @Override
    public void save(T entity) {
        logger.info("Saving user {} to the database.", entity);
        try{
            SessionFactoryMaker.getFactory().inTransaction(session -> session
                    .persist(entity));
        } catch (ConstraintViolationException ex){
            logger.warn("Error when trying to persist user in the database. Error: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void update(T entity) {
        logger.info("Updating user {} in the database.", entity);
        try{
            SessionFactoryMaker.getFactory().inTransaction(session -> session
                    .merge(entity));
        } catch (ConstraintViolationException ex){
            logger.warn("Error when trying to update user in the database. Error: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void deleteById(Long id) {
        logger.info("Removing user with id={} from the database.", id);
        Optional<T> user = findById(id);
        if(user.isEmpty()){
            logger.warn("T removal failed because user with id={} was not found", id);
            throw new EntityNotFoundException(String.format("T with id %d not found", id));
        }
        SessionFactoryMaker.getFactory().inTransaction(session -> session
                .remove(user.get()));
    }

    @Override
    public boolean existsById(Long id) {
        logger.info("Checking if user with id={} exists.", id);
        return findById(id).isPresent();
    }
}
