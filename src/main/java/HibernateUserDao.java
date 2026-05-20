import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class HibernateUserDao implements UserDao {
    @Override
    public Optional<User> findById(Long id){
        return Optional.ofNullable(SessionFactoryMaker.getFactory().fromTransaction(session -> session
                .find(User.class, id)));
    }

    @Override
    public List<User> findAll() {
        return SessionFactoryMaker.getFactory().fromTransaction(session -> session
                .createSelectionQuery("from User", User.class)
                .getResultList());
    }

    @Override
    public void save(User user) {
        SessionFactoryMaker.getFactory().inTransaction(session -> session
                .persist(user));
    }

    @Override
    public void updateById(Long id, User user) {
        Optional<User> foundUser = findById(id);
        if(foundUser.isEmpty()){
            throw new NoSuchElementException(String.format("Пользователь с id %d не найден."));
        }
        User userToUpdate = foundUser.get();
        userToUpdate.setName(user.getName());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setAge(user.getAge());
        SessionFactoryMaker.getFactory().inTransaction(session -> session
                .merge(userToUpdate));
    }

    @Override
    public void deleteById(Long id) {
        String hql = "delete from User where id = :id";
        SessionFactoryMaker.getFactory().inTransaction(session -> session
                .createQuery(hql)
                .setParameter("id", id)
                .executeUpdate());
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }
}
