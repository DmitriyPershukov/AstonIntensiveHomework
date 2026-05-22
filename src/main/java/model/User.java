package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @Column(unique = true, nullable = false)
    @Size(min = 4, max = 20)
    private String name;
    @Column(unique = true, nullable = false)
    @Size(min = 4, max = 20)
    @Email
    private String email;
    @Column(nullable = false)
    @Min(value = 18)
    private int age;
    @Column(name = "created_at",nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String name, String email, int age) {
        setName(name);
        setEmail(email);
        setAge(age);
    }

    public void setName(String name) {
        if (name.length() < 4){
            throw new IllegalArgumentException("Имя пользователя не должно быть короче 4 символов.");
        }
        if (name.length() > 20){
            throw new IllegalArgumentException("Имя пользователя не должно быть длиннее 20 символов.");
        }
        this.name = name;
    }

    public void setEmail(String email) {
        if (name.length() < 4){
            throw new IllegalArgumentException("Пароль не должнен быть короче 4 символов.");
        }
        if (name.length() > 20){
            throw new IllegalArgumentException("Пароль не должнен быть длиннее 20 символов.");
        }
        this.email = email;
    }

    public void setAge(int age) {
        if (age < 18){
            throw new IllegalArgumentException("Возраст пользователя не может быть меньше 18.");
        }
        this.age = age;
    }

    @Override
    public String toString() {
        return "model.User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", createdAt=" + createdAt +
                '}';
    }
}
