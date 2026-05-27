package persistence;

import lombok.AccessLevel;
import lombok.Setter;
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

    @Setter
    private static String url = "jdbc:postgresql://postgres:5432/" + System.getenv("POSTGRES_DB");

    @Setter
    private static String username = System.getenv("POSTGRES_USER");

    @Setter
    private static String password = System.getenv("POSTGRES_PW");
    public static synchronized SessionFactory getFactory() {
        if (factory == null) {
            try {
                logger.info("Starting SessionFactory initialization.");
                factory = new Configuration()
                    .configure()
                    .setProperty("hibernate.connection.url", url)
                    .setProperty("hibernate.connection.username", username)
                    .setProperty("hibernate.connection.password", password)
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