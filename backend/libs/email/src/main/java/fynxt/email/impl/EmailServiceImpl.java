package fynxt.email.impl;

import fynxt.email.EmailService;
import fynxt.email.config.EmailProperties;
import fynxt.email.constants.EmailExecutionStatus;
import fynxt.email.dto.EmailRequest;
import fynxt.email.dto.EmailResponse;
import fynxt.email.dto.EmailTemplateContent;
import fynxt.email.template.EmailTemplateService;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import com.azure.communication.email.EmailAsyncClient;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.core.util.polling.PollerFlux;

public class EmailServiceImpl implements EmailService {

	private final EmailProperties emailProperties;
	private final EmailTemplateService emailTemplateService;
	private final EmailAsyncClient emailClient;
	private final ExecutorService executorService;

	public EmailServiceImpl(
			EmailProperties emailProperties,
			EmailTemplateService emailTemplateService,
			EmailAsyncClient emailAsyncClient,
			ExecutorService executorService) {
		this.emailProperties = emailProperties;
		this.emailTemplateService = emailTemplateService;
		this.emailClient = emailAsyncClient;
		this.executorService = executorService;
	}

	@Override
	public EmailResponse sendTemplatedEmail(EmailRequest request) {
		EmailRequest preparedRequest = prepareEmailRequest(request);

		CompletableFuture.supplyAsync(
				() -> {
					return executeEmailSending(preparedRequest);
				},
				executorService);

		return EmailResponse.builder()
				.emailId(preparedRequest.getEmailId())
				.correlationId(preparedRequest.getCorrelationId())
				.executionStatus(EmailExecutionStatus.PENDING.name())
				.description(preparedRequest.getDescription())
				.isSuccess(null)
				.recipientCount(
						preparedRequest.getRecipients() != null
								? preparedRequest.getRecipients().size()
								: 0)
				.senderAddress(preparedRequest.getSenderAddress())
				.sentAt(LocalDateTime.now())
				.build();
	}

	private EmailRequest prepareEmailRequest(EmailRequest request) {
		String emailId = request.getEmailId();
		if (emailId == null || emailId.isEmpty()) {
			emailId = "email-" + UUID.randomUUID().toString();
		}

		String correlationId = request.getCorrelationId();
		if (correlationId == null || correlationId.isEmpty()) {
			correlationId = "corr-" + UUID.randomUUID().toString();
		}

		String senderAddress = request.getSenderAddress();
		if (senderAddress == null || senderAddress.isEmpty()) {
			senderAddress = emailProperties.senderAddress();
		}

		return EmailRequest.builder()
				.emailId(emailId)
				.correlationId(correlationId)
				.recipients(request.getRecipients())
				.templateId(request.getTemplateId())
				.templateData(request.getTemplateData())
				.senderAddress(senderAddress)
				.description(request.getDescription())
				.build();
	}

	private EmailResponse executeEmailSending(EmailRequest request) {
		LocalDateTime sentAt = LocalDateTime.now();

		try {
			if (emailClient == null) {
				throw new IllegalStateException("Email client is not configured");
			}

			if (request.getTemplateId() == null || request.getTemplateId().isEmpty()) {
				throw new IllegalArgumentException("Template ID is required for templated emails");
			}

			if (request.getRecipients() == null || request.getRecipients().isEmpty()) {
				throw new IllegalArgumentException("Recipients are required");
			}

			EmailMessage emailMessage = buildTemplatedEmailMessage(request);

			PollerFlux<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(emailMessage);

			EmailSendResult result = poller.blockLast().getValue();

			return EmailResponse.builder()
					.emailId(request.getEmailId())
					.correlationId(request.getCorrelationId())
					.executionStatus(EmailExecutionStatus.SENT.name())
					.messageId(result.getId())
					.sentAt(sentAt)
					.completedAt(LocalDateTime.now())
					.description(request.getDescription())
					.isSuccess(true)
					.recipientCount(request.getRecipients().size())
					.senderAddress(request.getSenderAddress())
					.build();

		} catch (Exception e) {
			return EmailResponse.builder()
					.emailId(request.getEmailId())
					.correlationId(request.getCorrelationId())
					.executionStatus(EmailExecutionStatus.FAILED.name())
					.errorMessage(e.getMessage())
					.sentAt(sentAt)
					.completedAt(LocalDateTime.now())
					.description(request.getDescription())
					.isSuccess(false)
					.recipientCount(
							request.getRecipients() != null
									? request.getRecipients().size()
									: 0)
					.senderAddress(request.getSenderAddress())
					.build();
		}
	}

	private EmailMessage buildTemplatedEmailMessage(EmailRequest request) {
		EmailMessage emailMessage = new EmailMessage();

		emailMessage.setSenderAddress(request.getSenderAddress());
		emailMessage.setToRecipients(request.getRecipients().toArray(new String[0]));

		EmailTemplateContent templateContent =
				emailTemplateService.generateTemplate(request.getTemplateId(), request.getTemplateData());

		emailMessage.setSubject(templateContent.getSubject());
		emailMessage.setBodyHtml(templateContent.getHtmlContent());
		emailMessage.setBodyPlainText(templateContent.getPlainTextContent());

		return emailMessage;
	}
}
