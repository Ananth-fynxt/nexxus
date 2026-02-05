package fynxt.brand.user.service.impl;

import fynxt.brand.config.properties.ApiProperties;
import fynxt.brand.user.dto.UpdatePasswordRequest;
import fynxt.brand.user.dto.UserRequest;
import fynxt.brand.user.entity.User;
import fynxt.brand.user.repository.UserRepository;
import fynxt.brand.user.service.UserService;
import fynxt.brand.user.service.mappers.UserMapper;
import fynxt.brand.user.util.PasswordUtil;
import fynxt.common.enums.ErrorCode;
import fynxt.common.util.CryptoUtil;
import fynxt.email.EmailService;
import fynxt.email.dto.EmailRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final CryptoUtil cryptoUtil;
	private final PasswordUtil passwordUtil;
	private final EmailService emailService;
	private final ApiProperties apiProperties;

	@Override
	@Transactional
	public UserRequest createUser(UserRequest request) {

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorCode.USER_ALREADY_EXISTS.getCode());
		}

		String generatedPassword = passwordUtil.generateStrongPassword();
		String hashedPassword;
		try {
			hashedPassword = cryptoUtil.encrypt(generatedPassword);
		} catch (Exception e) {
			throw new ResponseStatusException(
					HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.USER_PASSWORD_TOO_WEAK.getCode());
		}

		User user = userMapper.toUser(request);
		user.setPassword(hashedPassword);

		User savedUser = userRepository.save(user);

		sendUserCreationEmail(savedUser.getEmail(), generatedPassword);

		return userMapper.toUserRequest(savedUser);
	}

	@Override
	public UserRequest getUserById(Integer id) {

		User user = userRepository
				.findById(id)
				.orElseThrow(
						() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND.getCode()));

		return userMapper.toUserRequest(user);
	}

	@Override
	public UserRequest getUserByEmail(String email) {

		User user = userRepository
				.findByEmail(email)
				.orElseThrow(
						() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND.getCode()));

		return userMapper.toUserRequest(user);
	}

	@Override
	@Transactional
	public UserRequest updatePassword(Integer userId, UpdatePasswordRequest request) {

		User user = userRepository
				.findById(userId)
				.orElseThrow(
						() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND.getCode()));

		try {
			String decryptedPassword = cryptoUtil.decrypt(user.getPassword());
			if (!request.getCurrentPassword().equals(decryptedPassword)) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_CREDENTIALS.getCode());
			}
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_CREDENTIALS.getCode());
		}

		String newHashedPassword;
		try {
			newHashedPassword = cryptoUtil.encrypt(request.getNewPassword());
		} catch (Exception e) {
			throw new ResponseStatusException(
					HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.USER_PASSWORD_TOO_WEAK.getCode());
		}

		userMapper.updateUserPassword(request, user);
		user.setPassword(newHashedPassword);
		User savedUser = userRepository.save(user);

		return userMapper.toUserRequest(savedUser);
	}

	@Override
	public User findByEmailForAuthentication(String email) {

		return userRepository
				.findByEmail(email)
				.orElseThrow(
						() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.USER_NOT_FOUND.getCode()));
	}

	@Override
	public User findByIdForAuthentication(Integer id) {

		return userRepository
				.findById(id)
				.orElseThrow(
						() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.USER_NOT_FOUND.getCode()));
	}

	private void sendUserCreationEmail(String userEmail, String password) {
		try {

			Map<String, Object> templateData = new HashMap<>();
			templateData.put("userEmail", userEmail);
			templateData.put("password", password);
			templateData.put("loginUrl", apiProperties.frontendUrl() + "/login");
			templateData.put("supportEmail", "support@fynxt.brand.fynxt.io");
			templateData.put("companyName", "Nexxus Platform");

			EmailRequest emailRequest = EmailRequest.builder()
					.recipients(Arrays.asList(userEmail))
					.templateId("welcome-email")
					.templateData(templateData)
					.description("Welcome email for new user: " + userEmail)
					.build();

			emailService.sendTemplatedEmail(emailRequest);
		} catch (Exception e) {
		}
	}
}
