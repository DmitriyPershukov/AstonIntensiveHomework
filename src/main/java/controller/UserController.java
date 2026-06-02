package controller;

import jakarta.persistence.EntityNotFoundException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class UserController {
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void interactWithUser(){
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
                    displayAllUsers();
                    break;
                case 2:
                    handleDisplayUserById();
                    break;
                case 3:
                    handleCreateUser();
                    break;
                case 4:
                    handleUpdateUser();
                    break;
                case 5:
                    handleDeleteUser();
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

    private void displayAllUsers(){
        List<User> users = userService.getAllUsers();
        if(users.isEmpty()){
            System.out.println("Список пользователей пуст");
        } else {
            displayUsers(users);
        }
    }

    private void handleDeleteUser(){
        System.out.println("Введите id пользователя которого нужно удалить.");
        Optional<Long> id = readLongFromUser();
        if(id.isEmpty()){
            System.out.println("Ввод должен быть числом");
            return;
        }
        try{
            userService.deleteUser(id.get());
            System.out.println(String.format("Пользователь с id %d успешно удален.", id.get()));
        } catch (EntityNotFoundException ex){
            System.out.println(String.format("Пользователь с id %d не найден.", id.get()));
        }
    }

    private void handleCreateUser(){
        User newUser = readNewUserFromInput();
        if(newUser == null){
            System.out.println("Ошибка ввода пользователя.");
            return;
        }
        try{
            userService.createUser(newUser);
            System.out.println("Пользователь успешно сохранен");
        } catch (org.hibernate.exception.ConstraintViolationException
                 | jakarta.validation.ConstraintViolationException ex){
            System.out.println(String.format("При попытке добавить пользователя произошла ошибка: %s",
                    ex.getMessage()));
        }
    }

    private void handleUpdateUser(){
        System.out.println("Введите id пользователя которого нужно изменить.");
        Optional<Long> id = readLongFromUser();
        if(id.isEmpty()){
            System.out.println("Id должен быть числом.");
            return;
        }
        if(!userService.exists(id.get())){
            System.out.println(String.format("Пользователь с id %d не найден.", id.get()));
            return;
        }
        System.out.println("Введите новые значения для имени, почты и возраста через запятую.");
        User newUser = readNewUserFromInput();
        if(newUser == null){
            System.out.println("Ошибка ввода пользователя.");
            return;
        }
        try {
            userService.updateUser(id.get(), newUser);
            System.out.println(String.format("Пользователь с id %d успешно обновлен.", id.get()));
        } catch (EntityNotFoundException ex){
            System.out.println(String.format("Пользователь с id %d не найден.", id.get()));
        } catch (org.hibernate.exception.ConstraintViolationException
                 | jakarta.validation.ConstraintViolationException ex){
            System.out.println(String.format("При попытке изменить пользователя произошла ошибка: %s",
                    ex.getMessage()));
        }
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
    private void handleDisplayUserById(){
        System.out.println("Введите id пользователя");
        Optional<Long> id = readLongFromUser();
        if(id.isEmpty()){
            System.out.println("Id должен быть числом.");
            return;
        }
        try{
            User user = userService.getUserById(id.get());
            displayUsers(List.of(user));
        } catch (EntityNotFoundException ex){
            System.out.println(String.format("Пользователь с id %d не найден", id.get()));
        }
    }

    private void displayUsers(List<User> users){
        if(users.isEmpty()){
            System.out.println("В системе нет записей о пользователях");
            return;
        }
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
