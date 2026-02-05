package fynxt.brand.webhook.controller;

import fynxt.brand.auth.context.BrandEnvironmentContextHolder;
import fynxt.brand.webhook.dto.WebhookDto;
import fynxt.brand.webhook.service.WebhookService;
import fynxt.common.http.ApiResponse;
import fynxt.common.http.ResponseBuilder;
import fynxt.permission.annotations.RequiresPermission;
import fynxt.permission.annotations.RequiresScope;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
@Validated
@RequiresScope({"FI", "BRAND"})
@Tag(name = "Webhooks")
public class WebhookController {

	private final WebhookService webhookService;
	private final ResponseBuilder responseBuilder;

	@PostMapping
	@Operation(summary = "Create a new webhook")
	@RequiresPermission(module = "webhooks", action = "create")
	public ResponseEntity<ApiResponse<Object>> create(
			@Parameter(required = true) @Validated @RequestBody WebhookDto webhookDto) {
		return responseBuilder.created(webhookService.create(webhookDto), "Created successfully");
	}

	@GetMapping
	@Operation(summary = "Get all webhooks by brand and environment")
	@RequiresPermission(module = "webhooks", action = "read")
	public ResponseEntity<ApiResponse<Object>> readAll(
			@Parameter(hidden = true) @RequestHeader(value = "X-BRAND-ID", required = false) UUID brandId,
			@Parameter(hidden = true) @RequestHeader(value = "X-ENV-ID", required = false) UUID environmentId) {
		UUID brandIdValue = brandId != null ? brandId : BrandEnvironmentContextHolder.getBrandId();
		UUID environmentIdValue =
				environmentId != null ? environmentId : BrandEnvironmentContextHolder.getEnvironmentId();
		return responseBuilder.get(
				webhookService.readAll(brandIdValue, environmentIdValue), "Webhooks retrieved successfully");
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get webhook by ID")
	@RequiresPermission(module = "webhooks", action = "read")
	public ResponseEntity<ApiResponse<Object>> read(
			@Parameter(required = true, example = "webhook_001") @PathVariable("id") @NotBlank String id) {
		Short webhookId = Short.parseShort(id);
		return responseBuilder.get(webhookService.read(webhookId), "Success");
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an existing webhook")
	@RequiresPermission(module = "webhooks", action = "update")
	public ResponseEntity<ApiResponse<Object>> update(
			@Parameter(required = true, example = "webhook_001") @PathVariable("id") @NotBlank String id,
			@Parameter(required = true) @Validated @RequestBody WebhookDto webhookDto) {
		Short webhookId = Short.parseShort(id);
		webhookDto.setId(webhookId);
		return responseBuilder.updated(webhookService.update(webhookId, webhookDto), "Success");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a webhook")
	@RequiresPermission(module = "webhooks", action = "delete")
	public ResponseEntity<ApiResponse<Object>> delete(
			@Parameter(required = true, example = "webhook_001") @PathVariable("id") @NotBlank String id) {
		Short webhookId = Short.parseShort(id);
		webhookService.delete(webhookId);
		return responseBuilder.deleted("Webhook deleted successfully");
	}
}
