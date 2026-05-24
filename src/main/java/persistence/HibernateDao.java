package persistence;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class HibernateDao<T> implements DataAccessObject<T>{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Class<T> entityClass;

    public HibernateDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    @Override
    public Optional<T> findById(Long id){
        logger.info("Fetching {} with id={} from the database.", entityClass.getSimpleName(), id);
        return Optional.ofNullable(SessionFactoryMaker.getFactory().fromTransaction(session -> session
                .find(entityClass, id)));
    }

    @Override
    public List<T> findAll() {
        logger.info("Fetching all {} entities from the database.", entityClass.getSimpleName());
        return SessionFactoryMaker.getFactory().fromTransaction(session -> session
                .createSelectionQuery(String.format("from %s", entityClass.getSimpleName()), entityClass)
                .getResultList());
    }

    @Override
    public void save(T entity) {
        logger.info("Saving {} {} to the database.", entityClass.getSimpleName(), entity);
        try{
            SessionFactoryMaker.getFactory().inTransaction(session -> session
                    .persist(entity));
        } catch (ConstraintViolationException ex){
            logger.warn("Error when trying to persist {} in the database. Error: {}",
                    entityClass.getSimpleName(), ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void update(T entity) {
        logger.info("Updating {} {} in the database.", entityClass.getSimpleName(), entity);
        try{
            SessionFactoryMaker.getFactory().inTransaction(session -> session
                    .merge(entity));
        } catch (ConstraintViolationException ex){
            logger.warn("Error when trying to update {} in the database. Error: {}",
                    entityClass.getSimpleName(), ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void deleteById(Long id) {
        logger.info("Removing {} with id={} from the database.", entityClass.getSimpleName(), id);
        SessionFactoryMaker.getFactory().inTransaction(session -> {
            T entity = session.find(entityClass, id);
            if (entity == null){
                logger.warn("{} removal failed because {} with id={} was not found",
                        entityClass.getSimpleName(), entityClass.getSimpleName(), id);
                throw new EntityNotFoundException(String.format("%s with id %d not found",
                        entityClass.getSimpleName(), id));
            }
            session.remove(entity);
        });
    }

    @Override
    public boolean existsById(Long id) {
        logger.info("Checking if {} with id={} exists.", entityClass.getSimpleName(), id);
        return SessionFactoryMaker.getFactory().fromTransaction(session -> session
                .createSelectionQuery(String.format("select count(*) from %s where id = :id",
                        entityClass.getSimpleName())
                        , Long.class)
                .setParameter("id", id)
                .getResultList()
                .get(0)) == 1;
    }
}
