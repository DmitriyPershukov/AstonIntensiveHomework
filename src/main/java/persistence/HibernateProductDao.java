package persistence;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class HibernateProductDao implements DataAccessObject<Product> {
    private final static Logger logger = LoggerFactory.getLogger(HibernateProductDao.class);
    @Override
    public Optional<Product> findById(Long id){
        logger.info("Fetching product with id={} from the database.", id);
        return Optional.ofNullable(SessionFactoryMaker.getFactory().fromTransaction(session -> session
                .find(Product.class, id)));
    }

    @Override
    public List<Product> findAll() {
        logger.info("Fetching all products from the database.");
        return SessionFactoryMaker.getFactory().fromTransaction(session -> session
                .createSelectionQuery("from Product", Product.class)
                .getResultList());
    }

    @Override
    public void save(Product product) {
        logger.info("Saving product {} to the database.", product);
        try{
            SessionFactoryMaker.getFactory().inTransaction(session -> session
                    .persist(product));
        } catch (ConstraintViolationException ex){
            logger.warn("Error when trying to persist product in the database. Error: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void update(Product product) {
        logger.info("Updating product {} in the database.", product);
        try{
            SessionFactoryMaker.getFactory().inTransaction(session -> session
                    .merge(product));
        } catch (ConstraintViolationException ex){
            logger.warn("Error when trying to update product in the database. Error: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void deleteById(Long id) {
        logger.info("Removing product with id={} from the database.", id);
        Optional<Product> product = findById(id);
        if(product.isEmpty()){
            logger.warn("Product removal failed because product with id={} was not found", id);
            throw new EntityNotFoundException(String.format("Product with id %d not found", id));
        }
        SessionFactoryMaker.getFactory().inTransaction(session -> session
                .remove(product.get()));
    }

    @Override
    public boolean existsById(Long id) {
        logger.info("Checking if product with id={} exists.", id);
        return findById(id).isPresent();
    }
}
