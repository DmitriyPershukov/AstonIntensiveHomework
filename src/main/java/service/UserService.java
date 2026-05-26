package service;

import jakarta.persistence.EntityNotFoundException;
import model.User;
import persistence.DataAccessObject;

import java.util.List;

public class UserService {
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
        User user = userDao.findById(id)
                .orElseThrow(() ->
                new EntityNotFoundException(String.format("User with id=%s doesn't exist.", id)));
        user.setName(newUser.getName());
        user.setEmail(newUser.getEmail());
        user.setAge(newUser.getAge());
        userDao.update(user);
    }

    public User getUserById(Long id){
        return userDao.findById(id)
                .orElseThrow(() ->
                new EntityNotFoundException(String.format("User with id=%s doesn't exist.", id)));
    }

    public List<User> getAllUsers(){
        return userDao.findAll();
    }

    public boolean exists(Long id){
        return userDao.existsById(id);
    }
}
