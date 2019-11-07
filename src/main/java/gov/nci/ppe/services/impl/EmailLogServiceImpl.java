package gov.nci.ppe.services.impl;

import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;

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
	
	private String charSet = "UTF-8";

	@Autowired
	private EmailLogRepository emailLogRepository; 
	
	@Autowired
	private EmailServiceConfig emailServiceConfig;
	
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
	public String sendEmailAfterUploadingReport(String userFirstName, String recipientEmail, String patientName, String senderEmail,String subject, String htmlBody, String textBody) {
			String replaceStringWith[] = {userFirstName, patientName};
			String replaceThisString[] = {"%{FirstName}", "%{PatientName}"};
			String updatedTextBody = StringUtils.replaceEach(textBody, replaceThisString, replaceStringWith);
			String emailStatus = sendEmail(recipientEmail, senderEmail, subject, updatedTextBody, updatedTextBody);
			if(emailStatus.contains(CommonConstants.SUCCESS)) {
				logEmailStatus(recipientEmail, subject, updatedTextBody);
			}
			return emailStatus;
	}
	
	/**
	 * Method to actually send the email
	 * recipientEmail - Recipient's email address
	 * senderEmail - Sender's email address
	 * subject - Subject for the email
	 * htmlBody - The text for the body (in HTML format) of the email
	 * textBody - The text for the body of the email
	 * @return Success or Failure message as String
	 */
	private String sendEmail(String recipientEmail,String senderEmail,String subject, String htmlBody, String textBody) {
		try {
			AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.defaultClient();

			SendEmailRequest request = new SendEmailRequest()
					.withDestination(new Destination().withToAddresses(recipientEmail))
					.withMessage(new Message()
							.withBody(new Body().withHtml(new Content().withCharset(charSet).withData(htmlBody))
									.withText(new Content().withCharset(charSet).withData(textBody)))
							.withSubject(new Content().withCharset(charSet).withData(subject)))
					.withSource(senderEmail);
			SendEmailResult result = client.sendEmail(request);
			
			return StringUtils.join(CommonConstants.SUCCESS," : Email sent successfully!", result.getMessageId());
		} catch (Exception ex) {
			ex.printStackTrace();
			return StringUtils.join(CommonConstants.ERROR," : Error sending email. Error Message : ", ex.getMessage());
		}
	}
	
    /**
     * {@inheritDoc}
     */	
	@Override
	public String sendEmailNotification(String recipientEmail, String senderEmail, String subject, String htmlBody, String textBody) {
		String emailStatus = sendEmail(recipientEmail, senderEmail, subject, htmlBody, textBody);
		if (emailStatus.contains(CommonConstants.SUCCESS)) {
			logEmailStatus(recipientEmail, subject, textBody);
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
		String senderEmail = emailServiceConfig.getSenderEmailAddress();

		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith);
		String updatedTextBody = StringUtils.replaceEach(textBody, replaceThisString, replaceStringWith);


		String emailStatus = sendEmail(recipientEmail, senderEmail, subject, updatedHtmlBody, updatedTextBody);
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
		String textBody = emailServiceConfig.getEmailTextBodyForProviderPatientInvite();
		String senderEmail = emailServiceConfig.getSenderEmailAddress();

		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith);
		String updatedTextBody = StringUtils.replaceEach(textBody, replaceThisString, replaceStringWith);

		String emailStatus = sendEmail(recipientEmail, senderEmail, subject, updatedHtmlBody, updatedTextBody);
		if (emailStatus.contains(CommonConstants.SUCCESS)) {
			logEmailStatus(recipientEmail, subject, updatedHtmlBody);
		}
		return emailStatus;

	}	

}