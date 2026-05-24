package persistence;

import model.Order;
import model.Product;
import model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionFactoryMaker {
    private final static Logger logger = LoggerFactory.getLogger(SessionFactoryMaker.class);
    private static SessionFactory factory;
    public static synchronized SessionFactory getFactory() {
        if (factory == null) {
            try {
                logger.info("Starting SessionFactory initialization.");
                factory = new Configuration()
                    .configure()
                    .setProperty("hibernate.connection.url",
                            "jdbc:postgresql://postgres:5432/" + System.getenv("POSTGRES_DB"))
                    .setProperty("hibernate.connection.username", System.getenv("POSTGRES_USER"))
                    .setProperty("hibernate.connection.password", System.getenv("POSTGRES_PW"))
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Order.class)
                    .addAnnotatedClass(Product.class)
                    .buildSessionFactory();
                logger.info("Starting SessionFactory initialization finished successfully.");
            } catch (Exception ex) {
                logger.error("Session factory initialization failed with exception {}", ex.getMessage());
                throw new ExceptionInInitializerError(ex);
            }
        }
        return factory;
    }
}