package rest.api.example.user.dtos;

import rest.api.example.shared.AbstractConverter;
import rest.api.example.user.entities.User;

public class UserDTOConverter implements AbstractConverter<User, UserDTO> {

    @Override
    public User toEntity(UserDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        return user;
    }

    @Override
    public UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        return userDTO;
    }

}
