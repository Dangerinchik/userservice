package com.userservice.mapper;

import com.userservice.dto.UserDTO;
import com.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDTO toUserDTO(User user);

    User toUser(UserDTO userDTO);

    Page<UserDTO> toUserDTOPage(Page<User> users);
}
