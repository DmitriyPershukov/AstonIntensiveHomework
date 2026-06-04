package com.example.user_service;

import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Iterator;
import java.util.List;

@Controller
public class HelloController {
    UserRepository userRepository;

    public HelloController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @GetMapping("/hello")
    @ResponseBody
    public Iterable<User> hello() {
        User user = new User("Martha", "martha@gmail.com", 35);
        userRepository.save(user);
        return userRepository.findAll();
    }
}
