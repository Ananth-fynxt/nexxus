package fynxt.email.template;

import fynxt.email.dto.EmailTemplateContent;

import java.util.Map;

public interface EmailTemplateService {

	EmailTemplateContent generateTemplate(String templateId, Map<String, Object> templateData);
}
