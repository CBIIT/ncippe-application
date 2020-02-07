package gov.nci.ppe.services.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.repository.EmailLogRepository;
import gov.nci.ppe.services.EmailLogService;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-13
 */
@Component
public class EmailLogServiceImpl implements EmailLogService {

	private String charSet = "UTF-8";

	@Autowired
	private EmailLogRepository emailLogRepository;

	@Autowired
	private EmailServiceConfig emailServiceConfig;

	@Autowired
	private JavaMailSender nihMailSender;

	private static final Logger logger = LogManager.getLogger(EmailLogServiceImpl.class);

	public EmailLogServiceImpl() {
	}

	public EmailLogServiceImpl(EmailLogRepository emailLogRepository) {
		this.emailLogRepository = emailLogRepository;
	}

	/**
	 * This method will create an entry in the EmailLog table. recipientEmailId -
	 * Recipient's email address emailSubject - Subject for the email emailBody -
	 * The text for the body of the email
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
	public String sendEmailNotification(String recipientEmail, String senderEmail, String subject, String htmlBody,
			String textBody) {
		String emailStatus = sendEmail(recipientEmail, subject, htmlBody, true);
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

		String htmlBody = emailServiceConfig.getEmailHtmlBodyForPatientInvite()
				+ emailServiceConfig.getJoiningSignature();
		String subject = emailServiceConfig.getEmailSubjectForPatientInvite();
		String textBody = emailServiceConfig.getEmailTextBodyForPatientInvite();

		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith);
		StringUtils.replaceEach(textBody, replaceThisString, replaceStringWith);

		String emailStatus = sendEmail(recipientEmail, subject, updatedHtmlBody, true);
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

		String htmlBody = emailServiceConfig.getEmailHtmlBodyForProviderPatientInvite()
				+ emailServiceConfig.getThankYouForContributionSignature();
		String subject = emailServiceConfig.getEmailSubjectForProviderPatientInvite();
		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith);
		String emailStatus = sendEmail(recipientEmail, subject, updatedHtmlBody, true);
		if (emailStatus.contains(CommonConstants.SUCCESS)) {
			logEmailStatus(recipientEmail, subject, updatedHtmlBody);
		}
		return emailStatus;

	}

	@Override
	public String sendEmailToCRCOnNewPatient(String recipientEmail, String firstName) {
		String replaceStringWith[] = { firstName };
		String replaceThisString[] = { "%{SalutationFirstName}" };

		String htmlBody = emailServiceConfig.getEmailCRCAboutNewPatientDataFromOpenHtmlBody()
				+ emailServiceConfig.getThankYouParticipatingSignature();
		String subject = emailServiceConfig.getEmailCRCAboutNewPatientDataFromOpenSubject();
		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith);
		String emailStatus = sendEmail(recipientEmail, subject, updatedHtmlBody, true);
		if (emailStatus.contains(CommonConstants.SUCCESS)) {
			logEmailStatus(recipientEmail, subject, updatedHtmlBody);
		}
		return emailStatus;
	}

	private String sendEmail(String recipientEmail, String subject, String messageBody, boolean isHtmlFormat) {
		return sendEmail(recipientEmail, subject, messageBody, isHtmlFormat, null);
	}

	private String sendEmail(String recipientEmail, String subject, String messageBody, boolean isHtmlFormat,
			String signature) {
		// If there is a signature, append to the message body
		if (StringUtils.isNotBlank(signature)) {
			messageBody = messageBody + signature;
		}

		if (emailServiceConfig.getUseAWSSES()) {
			try {
				AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.defaultClient();

				SendEmailRequest request = new SendEmailRequest()
						.withDestination(new Destination().withToAddresses(recipientEmail))
						.withMessage(new Message()
								.withBody(new Body().withHtml(new Content().withCharset(charSet).withData(messageBody)))
								.withSubject(new Content().withCharset(charSet).withData(subject)))
						.withSource(emailServiceConfig.getSenderEmailAddress());
				SendEmailResult result = client.sendEmail(request);

				return StringUtils.join(CommonConstants.SUCCESS, " : Email sent successfully!", result.getMessageId());
			} catch (Exception ex) {
				logger.error(StringUtils.join(CommonConstants.ERROR, " : Failed to Send email "), ex);
				return StringUtils.join(CommonConstants.ERROR, " : Error sending email. Error Message : ",
						ex.getMessage());
			}

		} else {
			try {
				MimeMessage message = nihMailSender.createMimeMessage();
				MimeMessageHelper htmlMailHelper = new MimeMessageHelper(message, true);
				htmlMailHelper.setTo(recipientEmail);
				htmlMailHelper.setFrom(emailServiceConfig.getSenderEmailAddress());
				htmlMailHelper.setSubject(subject);
				htmlMailHelper.setText(messageBody, true);
				nihMailSender.send(message);
				logger.info("Send email Re: {} to recipient {}", subject, recipientEmail);
				return CommonConstants.SUCCESS;
			} catch (MailException | MessagingException e) {
				logger.error(StringUtils.join(CommonConstants.ERROR, " : Failed to Send email "), e);
				return StringUtils.join(CommonConstants.ERROR, " : Failed to Send email ", e.getMessage());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToInviteNonPatients(String recipientEmail, String firstName) {
		String replaceStringWith[] = { firstName };
		String replaceThisString[] = { "%{SalutationFirstName}" };
		String htmlBody = emailServiceConfig.getEmailHtmlBodyForNonPatientInvite()
				+ emailServiceConfig.getJoiningSignature();
		String subject = emailServiceConfig.getEmailSubjectForNonPatientInvite();
		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith);

		String emailStatus = sendEmail(recipientEmail, subject, updatedHtmlBody, true);
		if (emailStatus.contains(CommonConstants.SUCCESS)) {
			logEmailStatus(recipientEmail, subject, updatedHtmlBody);
		}
		return emailStatus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToCRCAndProvidersAfterUploadingBioMarkerReport(String salutationFirstName,
			String recipientEmail, String patientFullName, String patientId) {
		String replaceStringWith[] = { salutationFirstName, patientFullName, patientId };
		String replaceThisString[] = { "%{SalutationFirstName}", "%{FullName}", "%{PatientId}" };
		String subject = emailServiceConfig.getEmailCRCAndProvidersAboutUNewlyUploadedBiomarkerReportSubject();
		String htmlBody = emailServiceConfig.getEmailCRCAndProvidersAboutUNewlypUloadedBiomarkerReportHtmlBody()
				+ emailServiceConfig.getThankYouParticipatingSignature();
		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith);
		String updatedSubject = StringUtils.replaceEach(subject, replaceThisString, replaceStringWith);
		String emailStatus = sendEmail(recipientEmail, updatedSubject, updatedHtmlBody, true);
		if (emailStatus.contains(CommonConstants.SUCCESS)) {
			logEmailStatus(recipientEmail, subject, updatedHtmlBody);
		}
		return emailStatus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToPatientAfterUploadingReport(String recipientEmail, String userFirstName) {
		String replaceStringWith[] = { userFirstName };
		String replaceThisString[] = { "%{SalutationFirstName}" };
		String htmlBody = emailServiceConfig.getEmailUploadReportPatientHTMLBody();
		String signature = emailServiceConfig.getThankYouParticipatingSignature();
		String subject = emailServiceConfig.getEmailUploadReportPatientSubject();
		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith);

		String emailStatus = sendEmail(recipientEmail, subject, updatedHtmlBody, true, signature);
		if (emailStatus.contains(CommonConstants.SUCCESS)) {
			logEmailStatus(recipientEmail, subject, updatedHtmlBody);
		}
		return emailStatus;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToPatientAfterUploadingEconsent(String recipientEmail, String firstName) {
		String replaceStringWith[] = { firstName };
		String replaceThisString[] = { "%{SalutationFirstName}" };
		String htmlBody = emailServiceConfig.getEmailBodyInHtmlFormatForEConsent();
		String subject = emailServiceConfig.getEmailSubjectForEConsent();
		String signature = emailServiceConfig.getThankYouParticipatingSignature();
		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith) + signature;

		String emailStatus = sendEmail(recipientEmail, subject, updatedHtmlBody, true);
		if (emailStatus.contains(CommonConstants.SUCCESS)) {
			logEmailStatus(recipientEmail, subject, updatedHtmlBody);
		}
		return emailStatus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToAdminAfterFileUpload(Participant participant, String recipientEmail) {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		dtf.format(now);
		String[] dateTime = StringUtils.split(dtf.format(now), "-");

		/* Replace the variables in the EmailBody */
		String replaceStringWith[] = { participant.getFirstName(), participant.getLastName(),
				participant.getPatientId(), dateTime[0], dateTime[1] };
		String replaceThisString[] = { "%{FirstName}", "%{LastName}", "%{UserGUID}", "%{date}", "%{time}" };
		String updatedHtmlBody = StringUtils.replaceEach(emailServiceConfig.getEmailHtmlBodyForAdmin(),
				replaceThisString, replaceStringWith);

		/* Replace the variables in the Subject Line */
		String replaceSubjectStringWith[] = { participant.getPatientId() };
		String replaceThisStringForSubject[] = { "%{UserGUID}" };
		String subject = StringUtils.replaceEach(emailServiceConfig.getEmailSubjectForAdmin(),
				replaceThisStringForSubject, replaceSubjectStringWith);

		String emailStatus = sendEmail(recipientEmail, subject, updatedHtmlBody, true);
		if (emailStatus.contains(CommonConstants.SUCCESS)) {
			logEmailStatus(recipientEmail, subject, updatedHtmlBody);
		}
		return emailStatus;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToPatientWhenProviderChanges(String recipientEmail, String patientFirstName, String patientId) {
		String replaceStringWith[] = { patientFirstName , patientId };
		String replaceThisString[] = { "%{SalutationFirstName}", "%{PatientId}" };

		String htmlBody = emailServiceConfig.getEmailPatientWhenProvidersAreReplacedHtmlBody();
		String subject = emailServiceConfig.getEmailPatientWhenProvidersAreReplacedSubject();
		String signature = emailServiceConfig.getThankYouParticipatingSignature();
		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith);
		String emailStatus = sendEmail(recipientEmail, subject, updatedHtmlBody+signature, true);
		if (emailStatus.contains(CommonConstants.SUCCESS)) {
			logEmailStatus(recipientEmail, subject, updatedHtmlBody);
		}
		return emailStatus;
	}	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToPatientWhenCRCChanges(String recipientEmail, String patientirstName, String patientId) {
		String replaceStringWith[] = { patientirstName , patientId };
		String replaceThisString[] = { "%{SalutationFirstName}", "%{PatientId}" };

		String htmlBody = emailServiceConfig.getEmailPatientWhenCRCIsReplacedHtmlBody();
		String subject = emailServiceConfig.getEmailPatientWhenCRCIsReplacedSubject();
		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith);
		String signature = emailServiceConfig.getThankYouParticipatingSignature();
		String emailStatus = sendEmail(recipientEmail, subject, updatedHtmlBody+signature, true);
		if (emailStatus.contains(CommonConstants.SUCCESS)) {
			logEmailStatus(recipientEmail, subject, updatedHtmlBody);
		}
		return emailStatus;
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToCRCWhenPatientIsAdded(String recipientEmail, String CRCFullName) {
		String replaceStringWith[] = { CRCFullName};
		String replaceThisString[] = { "%{SalutationFirstName}"};	
		String subject = emailServiceConfig.getEmailCRCWhenPatientIsAddedSubject();
		String htmlBody = emailServiceConfig.getEmailCRCWhenPatientIsAddedHtmlBody();
		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith);
		String signature = emailServiceConfig.getThankYouForContributionSignature();
		String emailStatus = sendEmail(recipientEmail, subject, updatedHtmlBody+signature, true);
		if (emailStatus.contains(CommonConstants.SUCCESS)) {
			logEmailStatus(recipientEmail, subject, updatedHtmlBody);
		}
		return emailStatus;
	}
	
}