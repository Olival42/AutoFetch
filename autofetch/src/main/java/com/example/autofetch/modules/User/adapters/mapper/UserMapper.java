package com.example.autofetch.modules.User.adapters.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.example.autofetch.modules.User.application.web.dto.UserRegisterRequestDTO;
import com.example.autofetch.modules.User.application.web.dto.UserResponseDTO;
import com.example.autofetch.modules.User.domain.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface UserMapper {

    User toEntity(UserRegisterRequestDTO userRegisterRequestDTO);

    @Mapping(target = "accessToken", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    UserResponseDTO toDTO(User user);
}
