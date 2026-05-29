package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name="admins")
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class Admin extends User{
    @ElementCollection(targetClass = Permission.class)
    @CollectionTable(name = "admin_permissions", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "permission")
    @Enumerated(EnumType.STRING)
    private List<Permission> permissions;

    public Admin(String name, String email, Permission... permissions){
        super(name, email);
        this.permissions = List.of(permissions);
    }
}
