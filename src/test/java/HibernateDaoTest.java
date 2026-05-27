import jakarta.persistence.EntityNotFoundException;
import model.User;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import persistence.DataAccessObject;
import persistence.HibernateDao;
import persistence.SessionFactoryMaker;

import java.util.List;
import java.util.Optional;

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

    @BeforeEach
    void truncateTables(){
        SessionFactoryMaker.getFactory().inTransaction(session ->
                session.createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY CASCADE")
                        .executeUpdate());
    }

    @Test
    void testFindById(){
        User user = new User("John", "john@gmail.com", 35);
        SessionFactoryMaker.getFactory().inTransaction(session -> session.persist(user));
        Optional<User> result = userDao.findById(1L);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(user, result.get());
    }

    @Test
    void testFindByIdReturnsEmptyOptionalIfEntityDoesntExist(){
        Optional<User> result = userDao.findById(1L);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testFindAll(){
        List<User> users = List.of(
            new User("John", "john@gmail.com", 35),
            new User("Mary", "mary@gmail.com", 35),
            new User("Nick", "nick@gmail.com", 35)
        );
        SessionFactoryMaker.getFactory().inTransaction(session ->
                users.forEach(user -> session.persist(user)));
        Assertions.assertIterableEquals(users, userDao.findAll());
    }

    @Test
    void testFindAllReturnsEmptyListIfTableIsEmpty(){
        Assertions.assertIterableEquals(List.of(), userDao.findAll());
    }

    @Test
    void testSave(){
        User user = new User("John", "john@gmail.com", 35);
        userDao.save(user);
        User foundUser = SessionFactoryMaker.getFactory().fromTransaction(session ->
                session.find(User.class, 1L));
        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(user, foundUser);
    }

    @Test
    void testSaveThrowsConstraintException(){
        User user = new User("John", "john@gmail.com", 35);
        SessionFactoryMaker.getFactory().inTransaction(session ->
                session.persist(user));
        User userWithDuplicateName = new User("John", "george@gmail.com", 35);
        User userWithDuplicateEmail = new User("Chris", "john@gmail.com", 35);
        User userWithShortName = new User("A", "a@gmail.com", 35);
        User userWithWrongEmailFormat = new User("George", "George", 35);
        User userWithWrongAge = new User("Mike", "mike@gmail.com", -1);
        Assertions.assertThrows(org.hibernate.exception.ConstraintViolationException.class,
                () -> userDao.save(userWithDuplicateName));
        Assertions.assertThrows(org.hibernate.exception.ConstraintViolationException.class,
                () -> userDao.save(userWithDuplicateEmail));
        Assertions.assertThrows(jakarta.validation.ConstraintViolationException.class,
                () -> userDao.save(userWithShortName));
        Assertions.assertThrows(jakarta.validation.ConstraintViolationException.class,
                () -> userDao.save(userWithWrongEmailFormat));
        Assertions.assertThrows(jakarta.validation.ConstraintViolationException.class,
                () -> userDao.save(userWithWrongAge));
    }

    @Test
    void testUpdate(){
        User user = new User("John", "john@gmail.com", 35);
        SessionFactoryMaker.getFactory().inTransaction(session -> session.persist(user));
        user.setName("George");
        userDao.update(user);
        User foundUser = SessionFactoryMaker.getFactory().fromTransaction(session ->
                session.find(User.class, 1L));
        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals("George", foundUser.getName());
    }

    @Test
    void testUpdateThrowsConstraintException(){
        User user1 = new User("John", "john@gmail.com", 35);
        User user2 = new User("Mary", "mary@gmail.com", 35);
        SessionFactoryMaker.getFactory().inTransaction(session ->
                List.of(user1, user2).forEach(user -> session.persist(user)));
        user2.setName("John");
        Assertions.assertThrows(org.hibernate.exception.ConstraintViolationException.class,
                () -> userDao.update(user2));
        user2.setName("Mary");
        user2.setEmail("john@gmail.com");
        Assertions.assertThrows(org.hibernate.exception.ConstraintViolationException.class,
                () -> userDao.update(user2));
        user2.setEmail("mary@gmail.com");
        user2.setName("A");
        Assertions.assertThrows(jakarta.validation.ConstraintViolationException.class,
                () -> userDao.update(user2));
        user2.setName("Mary");
        user2.setEmail("Mary");
        Assertions.assertThrows(jakarta.validation.ConstraintViolationException.class,
                () -> userDao.update(user2));
        user2.setEmail("mary@gmail.com");
        user2.setAge(-1);
        Assertions.assertThrows(jakarta.validation.ConstraintViolationException.class,
                () -> userDao.update(user2));
    }

    @Test
    void testDeleteById(){
        User user = new User("John", "john@gmail.com", 35);
        SessionFactoryMaker.getFactory().inTransaction(session -> session.persist(user));
        userDao.deleteById(1L);
        Assertions.assertNull(SessionFactoryMaker.getFactory().fromTransaction(session ->
                session.find(User.class, 1L)));
    }

    @Test
    void testDeleteByIdThrowsExceptionWhenEntityWithIdDoesntExist(){
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                userDao.deleteById(1L));
    }

    @Test
    void testExistsByIdReturnsTrueIfEntityExists(){
        User user = new User("John", "john@gmail.com", 35);
        SessionFactoryMaker.getFactory().inTransaction(session -> session.persist(user));
        Assertions.assertTrue(userDao.existsById(1L));
    }

    @Test
    void testExistsByIdReturnsFalseIfEntityDoesntExist(){
        Assertions.assertFalse(userDao.existsById(1L));
    }
}
