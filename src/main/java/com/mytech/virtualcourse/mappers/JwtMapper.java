package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.JwtDTO;
import com.mytech.virtualcourse.security.CustomUserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface JwtMapper {
    @Mapping(source = "username", target = "username")
    @Mapping(source = "authorities", target = "roles", qualifiedByName = "authoritiesToRoles")
    @Mapping(target = "type", constant = "Bearer")
    JwtDTO toJwtDTO(CustomUserDetails userDetails);

    @Named("authoritiesToRoles")
    default List<String> authoritiesToRoles(Collection<?> authorities) {
        return authorities.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
