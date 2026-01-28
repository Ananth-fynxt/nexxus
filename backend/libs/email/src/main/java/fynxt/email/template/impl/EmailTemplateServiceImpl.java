package fynxt.email.template.impl;

import fynxt.email.dto.EmailTemplateContent;
import fynxt.email.template.EmailTemplateService;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class EmailTemplateServiceImpl implements EmailTemplateService {

	private final List<EmailTemplateService> templateServices;

	public EmailTemplateServiceImpl(List<EmailTemplateService> templateServices) {
		this.templateServices = templateServices.stream()
				.filter(service -> !(service instanceof EmailTemplateServiceImpl))
				.toList();
	}

	@Override
	public EmailTemplateContent generateTemplate(String templateId, Map<String, Object> templateData) {

		for (EmailTemplateService templateService : templateServices) {
			try {
				return templateService.generateTemplate(templateId, templateData);
			} catch (IllegalArgumentException e) {
				continue;
			}
		}

		throw new IllegalArgumentException("No template service available for template ID: " + templateId);
	}
}
