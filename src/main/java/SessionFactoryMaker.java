import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionFactoryMaker {
    private static SessionFactory factory;
    public static SessionFactory getFactory() {
        if (factory == null) {
            try {
                factory = new Configuration()
                    .configure()
                    .setProperty("hibernate.connection.url",
                            "jdbc:postgresql://postgres:5432/" + System.getenv("POSTGRES_DB"))
                    .setProperty("hibernate.connection.username", System.getenv("POSTGRES_USER"))
                    .setProperty("hibernate.connection.password", System.getenv("POSTGRES_PW"))
                    .addAnnotatedClass(User.class)
                    .buildSessionFactory();
            } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }
        }
        return factory;
    }
}