package gov.nci.ppe.services.impl;

import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import gov.nci.ppe.configurations.EmailServiceConfig;
import gov.nci.ppe.constants.CommonConstants;
import gov.nci.ppe.data.entity.EmailLog;
import gov.nci.ppe.data.repository.EmailLogRepository;
import gov.nci.ppe.services.EmailLogService;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-13
 */
@Component
public class EmailLogServiceImpl implements EmailLogService{
	
	@Autowired
	private EmailLogRepository emailLogRepository; 
	
	@Autowired
	private EmailServiceConfig emailServiceConfig;
	
	@Autowired
    private JavaMailSender nihMailSender;
	
	private static final Logger logger = LogManager.getLogger(EmailLogServiceImpl.class);

	public EmailLogServiceImpl() {}
	
	public EmailLogServiceImpl(EmailLogRepository emailLogRepository) {
		this.emailLogRepository = emailLogRepository;
	}
	
	/**
	 * This method will create an entry in the EmailLog table.
	 * recipientEmailId - Recipient's email address
	 * emailSubject - Subject for the email
	 * emailBody - The text for the body of the email
	 */
	private EmailLog logEmailStatus(String recipientEmailId, String emailSubject, String emailBody) {
		
		EmailLog emailLog = new EmailLog();
		emailLog.setRecipientAddress(recipientEmailId);
		emailLog.setEmailSubject(emailSubject);
		emailLog.setEmailBody(emailBody);
		emailLog.setEmailRequestedOn(new Timestamp(System.currentTimeMillis()));
		return this.emailLogRepository.save(emailLog);
	}

    /**
     * {@inheritDoc}
     */	
	@Override
	public String sendEmailAfterUploadingReport(String userFirstName, String recipientEmail, String patientName,
			String senderEmail, String subject, String htmlBody, String textBody) {
		String replaceStringWith[] = { userFirstName, patientName };
		String replaceThisString[] = { "%{FirstName}", "%{PatientName}" };
		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith);
		String emailStatus = sendEmail(recipientEmail, subject, updatedHtmlBody);
		if (emailStatus.contains(CommonConstants.SUCCESS)) {
			logEmailStatus(recipientEmail, subject, updatedHtmlBody);
		}
		return emailStatus;
	}
	
	
    /**
     * {@inheritDoc}
     */	
	@Override
	public String sendEmailNotification(String recipientEmail, String senderEmail, String subject, String htmlBody, String textBody) {
		String emailStatus = sendEmail(recipientEmail, subject, htmlBody);
		if (emailStatus.contains(CommonConstants.SUCCESS)) {
			logEmailStatus(recipientEmail, subject, htmlBody);
		}
		return emailStatus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToInvitePatient(String recipientEmail, String patientFirstName) {
		String replaceStringWith[] = { patientFirstName };
		String replaceThisString[] = { "%{SalutationFirstName}" };

		String htmlBody = emailServiceConfig.getEmailHtmlBodyForPatientInvite();
		String subject = emailServiceConfig.getEmailSubjectForPatientInvite();
		String textBody = emailServiceConfig.getEmailTextBodyForPatientInvite();

		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith);
		StringUtils.replaceEach(textBody, replaceThisString, replaceStringWith);


		String emailStatus = sendEmail(recipientEmail, subject, updatedHtmlBody);
		if (emailStatus.contains(CommonConstants.SUCCESS)) {
			logEmailStatus(recipientEmail, subject, updatedHtmlBody);
		}
		return emailStatus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToProviderOnPatientInvitation(String recipientEmail, String providerFirstName) {
		String replaceStringWith[] = { providerFirstName };
		String replaceThisString[] = { "%{SalutationFirstName}" };

		String htmlBody = emailServiceConfig.getEmailHtmlBodyForProviderPatientInvite();
		String subject = emailServiceConfig.getEmailSubjectForProviderPatientInvite();
		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith);
		String emailStatus = sendEmail(recipientEmail, subject, updatedHtmlBody);
		if (emailStatus.contains(CommonConstants.SUCCESS)) {
			logEmailStatus(recipientEmail, subject, updatedHtmlBody);
		}
		return emailStatus;

	}	
	
	private String sendEmail(String recipientEmail, String subject, String htmlBody) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(recipientEmail);
			message.setFrom(emailServiceConfig.getSenderEmailAddress());
			message.setSubject(subject);
			message.setText(htmlBody);
			nihMailSender.send(message);
			return CommonConstants.SUCCESS;
		} catch (MailException e) {
			logger.error(StringUtils.join(CommonConstants.ERROR, " : Failed to Send email "), e);
			return StringUtils.join(CommonConstants.ERROR, " : Failed to Send email ", e.getMessage());
		}
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToInviteNonPatients(String recipientEmail, String firstName) {
		String replaceStringWith[] = { firstName };
		String replaceThisString[] = { "%{SalutationFirstName}" };
		String htmlBody = emailServiceConfig.getEmailHtmlBodyForNonPatientInvite();
		String subject = emailServiceConfig.getEmailSubjectForNonPatientInvite();
		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith);

		String emailStatus = sendEmail(recipientEmail, subject, updatedHtmlBody);
		if (emailStatus.contains(CommonConstants.SUCCESS)) {
			logEmailStatus(recipientEmail, subject, updatedHtmlBody);
		}
		return emailStatus;

	}
}