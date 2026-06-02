package model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;

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
