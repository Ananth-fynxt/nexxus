package fynxt.brand.user.service.mappers;

import fynxt.brand.user.dto.UpdatePasswordRequest;
import fynxt.brand.user.dto.UserRequest;
import fynxt.brand.user.entity.User;
import fynxt.mapper.config.MapperCoreConfig;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperCoreConfig.class)
public interface UserMapper {

	UserRequest toUserRequest(User user);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "password", ignore = true)
	User toUser(UserRequest request);

	void updateUserPassword(UpdatePasswordRequest request, @MappingTarget User user);
}
