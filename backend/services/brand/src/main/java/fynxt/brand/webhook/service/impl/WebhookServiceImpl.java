package fynxt.brand.webhook.service.impl;

import fynxt.brand.webhook.dto.WebhookDto;
import fynxt.brand.webhook.entity.Webhook;
import fynxt.brand.webhook.repository.WebhookRepository;
import fynxt.brand.webhook.service.WebhookService;
import fynxt.brand.webhook.service.mappers.WebhookMapper;
import fynxt.common.enums.ErrorCode;
import fynxt.common.exception.ErrorCategory;
import fynxt.common.exception.TransactionException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

	private final WebhookRepository webhookRepository;
	private final WebhookMapper webhookMapper;

	@Override
	@Transactional
	public WebhookDto create(@Valid WebhookDto webhookDto) {
		verifyWebhookNotExists(webhookDto);

		Webhook webhook = webhookMapper.toWebhook(webhookDto);

		Webhook savedWebhook = webhookRepository.save(webhook);
		return webhookMapper.toWebhookDto(savedWebhook);
	}

	@Override
	public List<WebhookDto> readAll(UUID brandId, UUID environmentId) {
		return webhookRepository.findByBrandIdAndEnvironmentId(brandId, environmentId).stream()
				.map(webhookMapper::toWebhookDto)
				.collect(Collectors.toList());
	}

	@Override
	public WebhookDto read(Short id) {
		Webhook webhook = webhookRepository
				.findById(id)
				.orElseThrow(() -> new TransactionException(
						"Webhook not found", ErrorCode.RESOURCE_NOT_FOUND, ErrorCategory.NOT_FOUND));
		return webhookMapper.toWebhookDto(webhook);
	}

	@Override
	@Transactional
	public WebhookDto update(Short id, @Valid WebhookDto webhookDto) {
		Webhook existingWebhook = webhookRepository
				.findById(id)
				.orElseThrow(() -> new TransactionException(
						"Webhook not found", ErrorCode.RESOURCE_NOT_FOUND, ErrorCategory.NOT_FOUND));

		if (!existingWebhook.getBrandId().equals(webhookDto.getBrandId())
				|| !existingWebhook.getEnvironmentId().equals(webhookDto.getEnvironmentId())
				|| !existingWebhook.getStatusType().equals(webhookDto.getStatusType())) {
			verifyWebhookNotExists(webhookDto);
		}

		Webhook updatedWebhook = webhookMapper.toWebhook(webhookDto);
		updatedWebhook.setId(id);

		Webhook savedWebhook = webhookRepository.save(updatedWebhook);
		return webhookMapper.toWebhookDto(savedWebhook);
	}

	@Override
	@Transactional
	public void delete(Short id) {
		Webhook webhook = webhookRepository
				.findById(id)
				.orElseThrow(() -> new TransactionException(
						"Webhook not found", ErrorCode.RESOURCE_NOT_FOUND, ErrorCategory.NOT_FOUND));

		webhook.softDelete();
		webhookRepository.save(webhook);
	}

	private void verifyWebhookNotExists(WebhookDto webhookDto) {
		if (webhookRepository.existsByBrandIdAndEnvironmentIdAndStatusType(
				webhookDto.getBrandId(), webhookDto.getEnvironmentId(), webhookDto.getStatusType())) {
			throw new TransactionException(
					"Webhook already exists for this brand, environment and status type",
					ErrorCode.DUPLICATE_RESOURCE,
					ErrorCategory.CONFLICT);
		}
	}
}
