import model.User;
import persistence.DataAccessObject;
import persistence.HibernateDao;
import persistence.HibernateUserDao;
import ui.UserInterface;

public class Main {
    public static void main(String[] args){
        DataAccessObject<User> userDao = new HibernateDao(User.class);
        UserInterface userInterface = new UserInterface();
        userInterface.interactWithUser(userDao);
    }
}
