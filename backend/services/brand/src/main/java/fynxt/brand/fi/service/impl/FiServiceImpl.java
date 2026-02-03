package fynxt.brand.fi.service.impl;

import fynxt.brand.fi.dto.FiDto;
import fynxt.brand.fi.entity.Fi;
import fynxt.brand.fi.repository.FiRepository;
import fynxt.brand.fi.service.FiService;
import fynxt.brand.fi.service.mappers.FiMapper;
import fynxt.brand.user.dto.UserRequest;
import fynxt.brand.user.service.UserService;
import fynxt.common.enums.ErrorCode;
import fynxt.common.service.NameUniquenessService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FiServiceImpl implements FiService {

	private final FiRepository fiRepository;
	private final FiMapper fiMapper;
	private final UserService userService;
	private final NameUniquenessService nameUniquenessService;

	@Override
	@Transactional
	public FiDto create(FiDto dto) {
		nameUniquenessService.validateForCreate(name -> fiRepository.existsByName(name), "FI", dto.getName());
		verifyFiEmailExists(dto.getEmail());

		UserRequest createUserRequest =
				UserRequest.builder().email(dto.getEmail()).build();

		UserRequest createdUser = userService.createUser(createUserRequest);

		Fi fi = fiMapper.toFi(dto);
		fi.setUserId(createdUser.getId());

		return fiMapper.toFiDto(fiRepository.save(fi));
	}

	private void verifyFiEmailExists(String email) {
		if (fiRepository.existsByEmail(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorCode.FI_EMAIL_ALREADY_EXISTS.getCode());
		}
	}

	@Override
	public FiDto findByUserId(Integer userId) {
		Fi fi = fiRepository
				.findByUserId(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.FI_NOT_FOUND.getCode()));
		return fiMapper.toFiDto(fi);
	}
}
