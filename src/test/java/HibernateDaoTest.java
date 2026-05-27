import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import persistence.DataAccessObject;
import persistence.HibernateDao;
import persistence.SessionFactoryMaker;

import java.time.Duration;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;

public class HibernateDaoTest {
    public static final String POSTGRES_IMAGE = "postgres:18";
    @Container
    private static PostgreSQLContainer dbContainer =
            new PostgreSQLContainer(POSTGRES_IMAGE)
                    .withDatabaseName("test_db")
                    .withUsername("test_user")
                    .withPassword("test_pw");
    private DataAccessObject<User> userDao = new HibernateDao<>(User.class);

    @BeforeAll
    static void setUp(){
        dbContainer.start();
        SessionFactoryMaker.setUrl(dbContainer.getJdbcUrl());
        SessionFactoryMaker.setUsername(dbContainer.getUsername());
        SessionFactoryMaker.setPassword(dbContainer.getPassword());
    }

    @Test
    void testFindById(){
        User user = new User("John", "john@gmail.com", 35);
        SessionFactoryMaker.getFactory().inTransaction(session -> session.persist(user));
        Optional<User> result = userDao.findById(1L);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(user, result.get());
    }
}
