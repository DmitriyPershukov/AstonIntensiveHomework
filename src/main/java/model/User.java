package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(unique = true, nullable = false)
    @Size(min = 4, max = 20)
    @NotBlank
    @EqualsAndHashCode.Include
    private String name;

    @Column(unique = true, nullable = false)
    @Size(min = 4, max = 100)
    @NotBlank
    @Email
    @EqualsAndHashCode.Include
    private String email;

    @Column(nullable = false)
    @Min(value = 18)
    @EqualsAndHashCode.Include
    private int age;

    @Column(name = "created_at",nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = Order_.CUSTOMER,
            cascade = {
            CascadeType.PERSIST,
            CascadeType.REMOVE,
            CascadeType.MERGE})
    @Setter(AccessLevel.NONE)
    List<Order> orders = new ArrayList<>();

    @Embedded
    ShippingInformation shippingInformation;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String name, String email, int age) {
        this.name = name;
        this.email = email;
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
