package fynxt.common.service;

import fynxt.common.constants.ErrorCode;

import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NameUniquenessService {

	public void validateForCreate(Function<String, Boolean> existsCheck, String entityType, String name) {
		if (existsCheck.apply(name)) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					ErrorCode.DUPLICATE_RESOURCE.getCode() + ": " + entityType + " with name '" + name
							+ "' already exists");
		}
	}

	public void validateForUpdate(
			Function<String, Boolean> existsCheck, String entityType, String name, String currentName) {
		if (!name.equals(currentName) && existsCheck.apply(name)) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					ErrorCode.DUPLICATE_RESOURCE.getCode() + ": " + entityType + " with name '" + name
							+ "' already exists");
		}
	}
}
