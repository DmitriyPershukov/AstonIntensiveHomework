import java.util.List;
import java.util.Optional;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUserDao implements UserDao {
    final static Logger logger = LoggerFactory.getLogger(HibernateUserDao.class);
    @Override
    public Optional<User> findById(Long id){
        logger.info("Fetching user with id={} from the database.", id);
        return Optional.ofNullable(SessionFactoryMaker.getFactory().fromTransaction(session -> session
                .find(User.class, id)));
    }

    @Override
    public List<User> findAll() {
        logger.info("Fetching all users from the database.");
        return SessionFactoryMaker.getFactory().fromTransaction(session -> session
                .createSelectionQuery("from User", User.class)
                .getResultList());
    }

    @Override
    public void save(User user) {
        logger.info("Saving user {} to the database.", user);
        try{
            SessionFactoryMaker.getFactory().inTransaction(session -> session
                    .persist(user));
        } catch (ConstraintViolationException ex){
            logger.warn("Error when trying to persist user in the database. Error: {}", ex.getMessage());
            System.out.println(String.format("При попытке добавить пользователя произошла ошибка: %s",
                    ex.getMessage()));
        }
    }

    @Override
    public void update(User user) {
        logger.info("Merging user {} to the database.", user);
        try{
            SessionFactoryMaker.getFactory().inTransaction(session -> session
                    .merge(user));
        } catch (ConstraintViolationException ex){
            logger.warn("Error when trying to merge user in the database. Error: {}", ex.getMessage());
            System.out.println(String.format("При попытке изменить пользователя произошла ошибка: %s",
                    ex.getMessage()));
        }
    }

    @Override
    public void deleteById(Long id) {
        logger.info("Removing user with id={} from the database.", id);
        String hql = "delete from User where id = :id";
        SessionFactoryMaker.getFactory().inTransaction(session -> session
                .createQuery(hql)
                .setParameter("id", id)
                .executeUpdate());
    }

    @Override
    public boolean existsById(Long id) {
        logger.info("Checking if user with id={} exists.", id);
        return findById(id).isPresent();
    }
}
