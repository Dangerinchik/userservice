package com.userservice.mapper;

import com.userservice.dto.ResponseUserDTO;
import com.userservice.dto.UserDTO;
import com.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = CardInfoMapper.class)
public interface UserMapper {

    ResponseUserDTO toUserDTO(User user);

    User toUser(UserDTO userDTO);

    default Page<ResponseUserDTO> toUserDTOPage(Page<User> users){
        List<ResponseUserDTO> userList = users.stream().map(this::toUserDTO).toList();
        return new PageImpl<>(userList, users.getPageable(), users.getTotalElements());
    };
}
