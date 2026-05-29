package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = Order_.PRODUCTS, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Setter(AccessLevel.NONE)
    private List<Order> orders = new ArrayList<>();

    public Product(String name) {
        this.name = name;
    }
}
