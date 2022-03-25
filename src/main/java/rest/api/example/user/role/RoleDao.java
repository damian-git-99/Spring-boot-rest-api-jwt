package rest.api.example.user.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleDao extends JpaRepository<Role, Long> {

    @Query("SELECT r FROM Role r where r.role = ?1")
    Optional<Role> findRoleByRole(String role);

}
