package fynxt.brand.webhook.service.mappers;

import fynxt.brand.webhook.dto.WebhookDto;
import fynxt.brand.webhook.entity.Webhook;
import fynxt.mapper.config.MapperCoreConfig;

import org.mapstruct.Mapper;

@Mapper(config = MapperCoreConfig.class)
public interface WebhookMapper {

	WebhookDto toWebhookDto(Webhook webhook);

	Webhook toWebhook(WebhookDto webhookDto);
}
