package service;

import controller.UserController;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.DataAccessObject;
import persistence.HibernateDao;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final static Logger logger = LoggerFactory.getLogger(UserService.class);
    private DataAccessObject<User> userDao;

    public UserService(DataAccessObject<User> userDao){
        this.userDao = userDao;
    }
    public void deleteUser(Long id){
        userDao.deleteById(id);
    }

    public void createUser(User newUser){
        userDao.save(newUser);
    }

    public void updateUser(Long id, User newUser){
        Optional<User> user = userDao.findById(id);
        if(user.isEmpty()){
            logger.warn("Failed to find user with id={}.", id);
            throw new EntityNotFoundException();
        }
        User unwrappedUser = user.get();
        unwrappedUser.setName(newUser.getName());
        unwrappedUser.setEmail(newUser.getEmail());
        unwrappedUser.setAge(newUser.getAge());
        userDao.update(unwrappedUser);
    }

    public User getUserById(Long id){
        Optional<User> user = userDao.findById(id);
        if(user.isEmpty()){
            logger.warn("Failed to find user with id={}.", id);
            throw new EntityNotFoundException();
        }
        return user.get();
    }

    public List<User> getAllUsers(){
        return userDao.findAll();
    }

    public boolean exists(Long id){
        return userDao.existsById(id);
    }
}
