package rest.api.example.user.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import rest.api.example.user.role.Role;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    private String username;
    @NotEmpty
    @Email
    @Column(unique = true)
    private String email;
    @NotEmpty
    private String password;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "roles_id"})})
    @JsonIgnore
    private List<Role> roles = new ArrayList<>();

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public boolean addRole(Role role) {
        return roles.add(role);
    }
}
