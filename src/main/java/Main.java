import model.*;
import persistence.SessionFactoryMaker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        SessionFactoryMaker.getFactory().inTransaction(session -> {
            Admin admin = new Admin("George", "george@gmail.com",
                    Permission.GRANT_ADMIN_RIGHTS,
                    Permission.REVOKE_ADMIN_RIGHTS,
                    Permission.BAN_CUSTOMER);
            admin.setPermissionsChangedAt(LocalDateTime.now());
            Customer customer1 = new Customer("Florence", "florence@gmail.com",
                    "Moscow");
            Order order1 = new Order(customer1);
            Order order2 = new Order(customer1);
            Customer customer2 = new Customer("Mike", "mike@gmail.com",
                    "London");
            Order order3 = new Order(customer2);
            Order order4 = new Order(customer2);
            session.persist(admin);
            session.persist(customer1);
            session.persist(customer2);
        });
        SessionFactoryMaker.getFactory().inTransaction(session -> {
            List<User> users = session.createSelectionQuery("from User", User.class).getResultList();
            System.out.println(users.get(0) instanceof Admin); //prints true
            System.out.println(users.get(1) instanceof Customer); //prints true
            System.out.println(users.get(2) instanceof Customer); //prints true
        });
    }
}
