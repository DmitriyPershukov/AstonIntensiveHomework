package persistence;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import model.Order;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class HibernateOrderDao implements DataAccessObject<Order> {
    private final static Logger logger = LoggerFactory.getLogger(HibernateOrderDao.class);
    @Override
    public Optional<Order> findById(Long id){
        logger.info("Fetching order with id={} from the database.", id);
        return Optional.ofNullable(SessionFactoryMaker.getFactory().fromTransaction(session -> session
                .find(Order.class, id)));
    }

    @Override
    public List<Order> findAll() {
        logger.info("Fetching all orders from the database.");
        return SessionFactoryMaker.getFactory().fromTransaction(session -> session
                .createSelectionQuery("from Order", Order.class)
                .getResultList());
    }

    @Override
    public void save(Order order) {
        logger.info("Saving order {} to the database.", order);
        try{
            SessionFactoryMaker.getFactory().inTransaction(session -> session
                    .persist(order));
        } catch (ConstraintViolationException ex){
            logger.warn("Error when trying to persist order in the database. Error: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void update(Order order) {
        logger.info("Updating order {} in the database.", order);
        try{
            SessionFactoryMaker.getFactory().inTransaction(session -> session
                    .merge(order));
        } catch (ConstraintViolationException ex){
            logger.warn("Error when trying to update order in the database. Error: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void deleteById(Long id) {
        logger.info("Removing order with id={} from the database.", id);
        Optional<Order> order = findById(id);
        if(order.isEmpty()){
            logger.warn("Order removal failed because order with id={} was not found", id);
            throw new EntityNotFoundException(String.format("Order with id %d not found", id));
        }
        SessionFactoryMaker.getFactory().inTransaction(session -> session
                .remove(order.get()));
    }

    @Override
    public boolean existsById(Long id) {
        logger.info("Checking if order with id={} exists.", id);
        return findById(id).isPresent();
    }
}
