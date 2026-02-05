package fynxt.brand.webhook.service;

import fynxt.brand.webhook.dto.WebhookDto;

import java.util.List;
import java.util.UUID;

public interface WebhookService {

	WebhookDto create(WebhookDto webhookDto);

	List<WebhookDto> readAll(UUID brandId, UUID environmentId);

	WebhookDto read(Short id);

	WebhookDto update(Short id, WebhookDto webhookDto);

	void delete(Short id);
}
