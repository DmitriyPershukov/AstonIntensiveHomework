package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="customers")
@Getter
@NoArgsConstructor
@ToString(callSuper = true)
public class Customer extends User{

    @Setter
    @Column(name="shipping_address")
    @Size(min = 4, max = 20)
    private String shippingAddress;

    @OneToMany(mappedBy = Order_.CUSTOMER,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.REMOVE,
                    CascadeType.MERGE})
    List<Order> orders = new ArrayList<>();

    public Customer(String name, String email, String shippingAddress){
        super(name, email);
        this.shippingAddress = shippingAddress;
    }


}
