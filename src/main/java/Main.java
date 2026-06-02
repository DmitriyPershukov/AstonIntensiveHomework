import model.User;
import persistence.DataAccessObject;
import persistence.HibernateDao;
import controller.UserController;
import service.UserService;

public class Main {
    public static void main(String[] args){
        DataAccessObject<User> userDao = new HibernateDao(User.class);
        UserService userService = new UserService(userDao);
        UserController userController = new UserController(userService);
        userController.interactWithUser();
    }
}
