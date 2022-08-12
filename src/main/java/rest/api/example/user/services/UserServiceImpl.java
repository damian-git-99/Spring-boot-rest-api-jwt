package rest.api.example.user.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rest.api.example.security.exceptions.ServerErrorException;
import rest.api.example.user.entities.User;
import rest.api.example.user.daos.UserDao;
import rest.api.example.user.exceptions.EmailAlreadyExistsException;
import rest.api.example.user.exceptions.UserNotFoundException;
import rest.api.example.user.role.Role;
import rest.api.example.user.role.RoleDao;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao, RoleDao roleDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userDao.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                user.getRoles());
    }

    @Override
    public User findUserByEmail(String email) {
        return userDao.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public void signUp(User user) {

        if (userDao.findUserByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("the email already exists");
        }

        Role role = roleDao.findRoleByRole("USER")
                .orElseThrow(() -> new ServerErrorException("an error occurred on the server"));

        if (!user.addRole(role)) {
            throw new ServerErrorException("an error occurred on the server");
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userDao.save(user);
    }

    @Override
    public List<User> findAllUsers() {
        return userDao.findAll();
    }

    @Override
    public Optional<User> findUserById(Long userId) {
        return userDao.findById(userId);
    }

    @Override
    public void deleteUserById(Long id) {
        userDao.deleteById(id);
    }

    @Override
    public User updateUser(Long id, User newUser) {
        User userToBeUpdated = findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User Not found"));
        userToBeUpdated.setUsername(newUser.getUsername());
        String email = newUser.getEmail();

        if (!email.equals(userToBeUpdated.getEmail())) {
            if (userDao.findUserByEmail(email).isPresent()) {
                throw new EmailAlreadyExistsException("the email already exists");
            }
            userToBeUpdated.setEmail(email);
        }

        String password = newUser.getPassword() != null
                ? passwordEncoder.encode(newUser.getPassword())
                : userToBeUpdated.getPassword();

        userToBeUpdated.setPassword(password);
        return userDao.save(userToBeUpdated);
    }

}
