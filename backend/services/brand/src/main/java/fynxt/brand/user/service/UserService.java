package fynxt.brand.user.service;

import fynxt.brand.user.dto.UpdatePasswordRequest;
import fynxt.brand.user.dto.UserRequest;
import fynxt.brand.user.entity.User;

public interface UserService {

	UserRequest createUser(UserRequest request);

	UserRequest getUserById(Integer id);

	UserRequest getUserByEmail(String email);

	UserRequest updatePassword(Integer userId, UpdatePasswordRequest request);

	User findByEmailForAuthentication(String email);

	User findByIdForAuthentication(Integer id);
}
