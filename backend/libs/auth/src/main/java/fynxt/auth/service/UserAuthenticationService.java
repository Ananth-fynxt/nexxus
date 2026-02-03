package fynxt.auth.service;

import java.util.Map;

public interface UserAuthenticationService {

	Map<String, Object> authenticateUser(String email, String password);

	Map<String, Object> getUserInfoById(Integer userId);
}
