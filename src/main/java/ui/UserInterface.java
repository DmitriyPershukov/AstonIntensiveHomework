package ui;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.UserDao;

import java.util.*;

public class UserInterface {
    final static Logger logger = LoggerFactory.getLogger(UserInterface.class);
    public void interactWithUser(UserDao userDao){
        logger.info("Starting user console interaction.");
        while(true){
            System.out.println("Введите код операции:");
            System.out.println("1. Получить список всех пользователей.");
            System.out.println("2. Найти пользователя по id.");
            System.out.println("3. Добавить нового пользователя.");
            System.out.println("4. Изменить пользователя по id.");
            System.out.println("5. Удалить пользователя по id.");
            System.out.println("6. Закрыть программу.");
            Optional<Integer> actionCode = readIntegerFromUser();
            if(actionCode.isEmpty()){
                System.out.println("Ввод должен быть числом.");
                continue;
            }
            switch(actionCode.get()){
                case 1:
                    displayUsers(userDao.findAll());
                    break;
                case 2:
                    handleDisplayUserById(userDao);
                    break;
                case 3:
                    handleCreateUser(userDao);
                    break;
                case 4:
                    handleUpdateUser(userDao);
                    break;
                case 5:
                    handleDeleteUser(userDao);
                    break;
                case 6:
                    logger.info("Shutting down.");
                    System.exit(0);
                default:
                    System.out.println("Неверный код команды.");
                    break;
            }

        }
    }

    private void handleDeleteUser(UserDao userDao){
        System.out.println("Введите id пользователя которого нужно удалить.");
        Optional<Long> id = readLongFromUser();
        if(id.isEmpty()){
            System.out.println("Ввод должен быть числом");
            return;
        }
        if(!userDao.existsById(id.get())){
            logger.warn("Failed to find user with id={}.", id.get());
            System.out.println(String.format("Пользователь с id %d не найден.", id.get()));
            return;
        }
        userDao.deleteById(id.get());
    }

    private void handleCreateUser(UserDao userDao){
        User newUser = readNewUserFromInput();
        if(newUser == null){
            System.out.println("Ошибка ввода пользователя.");
            return;
        }
        userDao.save(newUser);
    }

    private void handleUpdateUser(UserDao userDao){
        System.out.println("Введите id пользователя которого нужно изменить.");
        Optional<Long> id = readLongFromUser();
        if(id.isEmpty()){
            System.out.println("Id должен быть числом.");
            return;
        }
        Optional<User> user = userDao.findById(id.get());
        if(user.isEmpty()){
            logger.warn("Failed to find user with id={}.", id.get());
            System.out.println(String.format("Пользователь с id %d не найден.", id.get()));
            return;
        }
        User unwrappedUser = user.get();
        System.out.println("Текущее состояние пользователя");
        displayUsers(List.of(unwrappedUser));
        System.out.println("Введите новые значения для имени, почты и возраста через запятую.");
        User newUser = readNewUserFromInput();
        if(newUser == null){
            System.out.println("Ошибка ввода пользователя.");
            return;
        }
        unwrappedUser.setName(newUser.getName());
        unwrappedUser.setEmail(newUser.getEmail());
        unwrappedUser.setAge(newUser.getAge());
        userDao.update(unwrappedUser);
    }

    private User readNewUserFromInput(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите имя пользователя:");
        String name = scanner.nextLine();
        System.out.println("Введите почту пользователя:");
        String email = scanner.nextLine();
        System.out.println("Введите возраст пользователя:");
        Optional<Integer> age = readIntegerFromUser();
        if(age.isEmpty()){
            System.out.println("Возраст должен быть числом");
            return null;
        }
        try{
            return new User(name, email, age.get());
        } catch (IllegalArgumentException ex){
            logger.warn("Failed to create new user object based on input");
            System.out.println(ex.getMessage());
            return null;
        }
    }
    private void handleDisplayUserById(UserDao userDao){
        System.out.println("Введите id пользователя");
        Optional<Long> id = readLongFromUser();
        if(id.isEmpty()){
            System.out.println("Id должен быть числом.");
            return;
        }
        Optional<User> user = userDao.findById(id.get());
        if(user.isEmpty()){
            logger.warn("Failed to find user with id={}.", id.get());
            System.out.println(String.format("Пользователь с id %d не найден", id.get()));
            return;
        }
        displayUsers(List.of(user.get()));
    }

    private void displayUsers(List<User> users){
        System.out.println("id, имя, почта, возраст, дата регистрации");
        users.forEach(user ->
                System.out.println(String
                    .format("%d, %s, %s, %d, %s",
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getAge(),
                        user.getCreatedAt())));
    }

    private Optional<Integer> readIntegerFromUser(){
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();
        try{
            return Optional.of(Integer.valueOf(userInput));
        } catch (NumberFormatException ex){
            logger.warn("Failed to convert user input to integer. model.User input={}", userInput);
            return Optional.empty();
        }
    }

    private Optional<Long> readLongFromUser(){
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();
        try{
            return Optional.of(Long.valueOf(userInput));
        } catch (NumberFormatException ex){
            logger.warn("Failed to convert user input to long. model.User input={}", userInput);
            return Optional.empty();
        }
    }
}
