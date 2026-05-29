import model.*;
import persistence.DataAccessObject;
import persistence.HibernateDao;
import persistence.SessionFactoryMaker;

import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        SessionFactoryMaker.getFactory().inTransaction(session -> {
            Admin admin = new Admin("George", "george@gmail.com",
                    Permission.GRANT_ADMIN_RIGHTS,
                    Permission.REVOKE_ADMIN_RIGHTS,
                    Permission.BAN_CUSTOMER);
            Customer customer1 = new Customer("Florence", "florence@gmail.com");
            Order order1 = new Order(customer1);
            Order order2 = new Order(customer1);
            Customer customer2 = new Customer("Mike", "mike@gmail.com");
            Order order3 = new Order(customer2);
            Order order4 = new Order(customer2);
            session.persist(admin);
            session.persist(customer1);
            session.persist(customer2);
        });
        System.out.println("Press enter to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }
}
