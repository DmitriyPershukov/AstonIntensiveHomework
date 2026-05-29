package model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@ToString(callSuper = true)
public class Customer extends User{
    @OneToMany(mappedBy = Order_.CUSTOMER,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.REMOVE,
                    CascadeType.MERGE})
    List<Order> orders = new ArrayList<>();

    public Customer(String name, String email){
        super(name, email);
    }
}
