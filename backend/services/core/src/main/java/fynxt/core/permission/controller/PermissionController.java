package fynxt.core.permission.controller;

import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;
import fynxt.core.permission.service.PermissionModuleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions")
public class PermissionController {

	private final PermissionModuleService permissionModuleService;
	private final ResponseBuilder responseBuilder;

	@GetMapping("/modules")
	@Operation(summary = "Get all available permission modules")
	public ResponseEntity<ApiResponse<Object>> getAvailableModules() {
		return responseBuilder.success(permissionModuleService.getAvailableModules());
	}
}
