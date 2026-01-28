package fynxt.email;

import fynxt.email.dto.EmailRequest;
import fynxt.email.dto.EmailResponse;

public interface EmailService {

	EmailResponse sendTemplatedEmail(EmailRequest request);
}
