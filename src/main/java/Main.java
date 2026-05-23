import model.User;
import persistence.DataAccessObject;
import persistence.HibernateUserDao;
import ui.UserInterface;

public class Main {
    public static void main(String[] args){
        DataAccessObject<User> userDao = new HibernateUserDao();
        UserInterface userInterface = new UserInterface();
        userInterface.interactWithUser(userDao);
    }
}
