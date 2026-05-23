package persistence;

import model.Order;
import model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionFactoryMaker {
    private final static Logger logger = LoggerFactory.getLogger(SessionFactoryMaker.class);
    private static SessionFactory factory;
    public static SessionFactory getFactory() {
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
                    .buildSessionFactory();
                logger.info("Starting SessionFactory initialization finished successfully.");
            } catch (Exception e) {
                logger.error("Session factory initialization failed with exception {}", e.getMessage());
                throw new ExceptionInInitializerError(e);
            }
        }
        return factory;
    }
}