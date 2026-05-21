import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        UserDao userDao = new HibernateUserDao();
        UserInterface userInterface = new UserInterface();
        userInterface.interactWithUser(userDao);
    }
}
