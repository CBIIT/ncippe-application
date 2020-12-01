package gov.nci.ppe.services.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
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

import gov.nci.ppe.constants.CommonConstants;
import gov.nci.ppe.constants.CommonConstants.LanguageOption;
import gov.nci.ppe.constants.EmailConstants;
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
@ConfigurationProperties("email")
public class EmailLogServiceImpl implements EmailLogService {

	private String charSet = "UTF-8";

	@Autowired
	private EmailLogRepository emailLogRepository;

	@Autowired
	private JavaMailSender nihMailSender;

	@Autowired
	private MessageSource messageSource;

	@Value("${email.hostname}")
	private String hostname;

	@Value("${email.use.aws}")
	private boolean useAWSSES;

	@Value("${email.sender.address}")
	private String senderEmailAddress;

	@Value("${email.restrict.outgoing.domain}")
	private boolean restrictOutgoingEmailDomain;

	@Value("${email.allowed.outgoing.domain}")
	private List<String> allowedEmailDomains;

	@Value("${email.forward.restricted.emails.to}")
	private String defaultEmailAddress;

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
		emailLog.setEmailRequestedOn(LocalDateTime.now());
		return this.emailLogRepository.save(emailLog);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailNotification(String recipientEmail, String senderEmail, String subject, String htmlBody) {
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
	public String sendEmailToInvitePatient(String recipientEmail, String patientFirstName,
			LanguageOption preferredLanguage) {
		String replaceStringWith[] = { hostname, patientFirstName };

		return sendEmailAndLogStatus(recipientEmail, EmailConstants.PATIENT_INVITE_PORTAL_BODY,
				EmailConstants.PATIENT_INVITE_PORTAL_SUBJECT, EmailConstants.JOINING_SIGNATURE, replaceStringWith,
				preferredLanguage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToProviderOnPatientInvitation(String recipientEmail, String providerFirstName,
			LanguageOption preferredLanguage) {
		String replaceStringWith[] = { hostname, providerFirstName };
		return sendEmailAndLogStatus(recipientEmail, EmailConstants.PROVIDER_PATIENT_ADDED_BODY,
				EmailConstants.PROVIDER_PATIENT_ADDED_SUBJECT, EmailConstants.CONTRIBUTING_SIGNATURE, replaceStringWith,
				preferredLanguage);

	}

	private String sendEmailAndLogStatus(String recipientEmail, String emailBodyCode, String subjectCode,
			String signatureCode, String[] replaceStringWith, LanguageOption preferredLanguage) {
		Locale locale = preferredLanguage.getLocale();
		String htmlBody = messageSource.getMessage(emailBodyCode, replaceStringWith, locale);
		String subject = messageSource.getMessage(subjectCode, null, locale);
		String signature = messageSource.getMessage(signatureCode, null, locale);

		String updatedHtmlBody = htmlBody + signature;
		String emailStatus = sendEmail(recipientEmail, subject, updatedHtmlBody, true);
		if (emailStatus.contains(CommonConstants.SUCCESS)) {
			logEmailStatus(recipientEmail, subject, updatedHtmlBody);
		}
		return emailStatus;
	}

	@Override
	public String sendEmailToCRCOnNewPatient(String recipientEmail, String firstName,
			LanguageOption preferredLanguage) {
		String replaceStringWith[] = { hostname, firstName };

		return this.sendEmailAndLogStatus(recipientEmail, EmailConstants.CRC_PATIENT_ADDED_FROM_OPEN_BODY,
				EmailConstants.CRC_PATIENT_ADDED_FROM_OPEN_SUBJECT, EmailConstants.CONTRIBUTING_SIGNATURE,
				replaceStringWith, preferredLanguage);
	}

	private String sendEmail(String recipientEmail, String subject, String messageBody, boolean isHtmlFormat) {
		return sendEmail(recipientEmail, subject, messageBody, isHtmlFormat, null);
	}

	private String sendEmail(String recipientEmail, String subject, String messageBody, boolean isHtmlFormat,
			String signature) {

		// Check if restrict email domains is on.
		if (restrictOutgoingEmailDomain) {
			// Check if the recipient Email is part of the allowed email
			String domain = recipientEmail.split("@")[1];
			logger.info("Checking if allowed to send email to " + domain);
			if (!allowedEmailDomains.contains(domain)) {
				logger.info("Replacing recipient email address " + recipientEmail + " with " + defaultEmailAddress);
				recipientEmail = defaultEmailAddress;
			}
		}
		// If there is a signature, append to the message body
		if (StringUtils.isNotBlank(signature)) {
			messageBody = messageBody + signature;
		}

		if (useAWSSES) {
			try {
				AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.defaultClient();

				SendEmailRequest request = new SendEmailRequest()
						.withDestination(new Destination().withToAddresses(recipientEmail))
						.withMessage(new Message()
								.withBody(new Body().withHtml(new Content().withCharset(charSet).withData(messageBody)))
								.withSubject(new Content().withCharset(charSet).withData(subject)))
						.withSource(senderEmailAddress);
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
				htmlMailHelper.setFrom(senderEmailAddress);
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
	public String sendEmailToInviteNonPatients(String recipientEmail, String firstName,
			LanguageOption preferredLanguage) {
		String replaceStringWith[] = { hostname, firstName };

		return this.sendEmailAndLogStatus(recipientEmail, EmailConstants.CRC_PROVIDER_ADD_FROM_OPEN_BODY,
				EmailConstants.CRC_PROVIDER_ADD_FROM_OPEN_SUBJECT, EmailConstants.JOINING_SIGNATURE, replaceStringWith,
				preferredLanguage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToCRCAndProvidersAfterUploadingBioMarkerReport(String salutationFirstName,
			String recipientEmail, String patientFullName, LanguageOption preferredLanguage) {
		String replaceStringWith[] = { hostname, salutationFirstName, patientFullName };

		return this.sendEmailAndLogStatus(recipientEmail, EmailConstants.CRC_PROVIDER_UPLOAD_REPORT_BODY,
				EmailConstants.CRC_PROVIDER_UPLOAD_REPORT_SUBJECT, EmailConstants.CONTRIBUTING_SIGNATURE,
				replaceStringWith, preferredLanguage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToPatientAfterUploadingReport(String recipientEmail, String userFirstName,
			LanguageOption preferredLanguage) {
		String replaceStringWith[] = { hostname, userFirstName };
		return sendEmailAndLogStatus(recipientEmail, EmailConstants.PATIENT_UPLOAD_REPORT_BODY,
				EmailConstants.PATIENT_UPLOAD_REPORT_SUBJECT, EmailConstants.PARTICIPATING_SIGNATURE, replaceStringWith,
				preferredLanguage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToPatientAfterUploadingEconsent(String recipientEmail, String firstName,
			LanguageOption preferredLanguage) {
		final String replaceStringWith[] = { hostname, firstName };
		return sendEmailAndLogStatus(recipientEmail, EmailConstants.PATIENT_UPLOAD_ECONSENT_BODY,
				EmailConstants.PATIENT_UPLOAD_ECONSENT_SUBJECT, EmailConstants.PARTICIPATING_SIGNATURE,
				replaceStringWith, preferredLanguage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToAdminAfterFileUpload(Participant participant, String recipientEmail,
			LanguageOption preferredLanguage, String fileName) {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		dtf.format(now);
		String[] dateTime = StringUtils.split(dtf.format(now), "-");

		/* Replace the variables in the EmailBody */
		String replaceStringWith[] = { participant.getFirstName(), participant.getLastName(),
				participant.getPatientId(), dateTime[0], dateTime[1], fileName };

		/* Replace the variables in the Subject Line */
		String replaceSubjectStringWith[] = { participant.getPatientId() };

		Locale locale = preferredLanguage.getLocale();

		String htmlBody = messageSource.getMessage(EmailConstants.LAB_ADMIN_UPLOAD_REPORT_BODY, replaceStringWith,
				locale);
		String subject = messageSource.getMessage(EmailConstants.LAB_ADMIN_UPLOAD_REPORT_SUBJECT,
				replaceSubjectStringWith, locale);

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
	public String sendEmailToPatientWhenProviderChanges(String recipientEmail, String patientFirstName,
			String patientId, LanguageOption preferredLanguage) {
		String replaceStringWith[] = { hostname, patientFirstName, patientId };

		return this.sendEmailAndLogStatus(recipientEmail, EmailConstants.PATIENT_CHANGE_PROVIDER_BODY,
				EmailConstants.PATIENT_CHANGE_PROVIDER_SUBJECT, EmailConstants.PARTICIPATING_SIGNATURE,
				replaceStringWith, preferredLanguage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToPatientWhenCRCChanges(String recipientEmail, String patientirstName, String patientId,
			LanguageOption preferredLanguage) {
		String replaceStringWith[] = { hostname, patientirstName, patientId };

		return this.sendEmailAndLogStatus(recipientEmail, EmailConstants.PATIENT_CHANGE_CRC_BODY,
				EmailConstants.PATIENT_CHANGE_CRC_SUBJECT, EmailConstants.PARTICIPATING_SIGNATURE, replaceStringWith,
				preferredLanguage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToCRCWhenPatientIsAdded(String recipientEmail, String CRCFullName,
			LanguageOption preferredLanguage) {
		String replaceStringWith[] = { hostname, CRCFullName };

		return this.sendEmailAndLogStatus(recipientEmail, EmailConstants.CRC_PATIENT_ADDED_BODY,
				EmailConstants.CRC_PATIENT_ADDED_SUBJECT, EmailConstants.CONTRIBUTING_SIGNATURE, replaceStringWith,
				preferredLanguage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToPatientAfterCRCWithdrawsPatient(String firstName, String lastName, String salutationName,
			String emailId, String questionAnswers, LanguageOption preferredLanguage) {

		final String replaceStringWith[] = { hostname, firstName, lastName, salutationName, questionAnswers };

		return this.sendEmailAndLogStatus(emailId, EmailConstants.PATIENT_CRC_WITHDRAW_BODY,
				EmailConstants.PATIENT_CRC_WITHDRAW_SUBJECT, EmailConstants.CONTRIBUTING_SIGNATURE, replaceStringWith,
				preferredLanguage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToCRCAfterParticipantWithdraws(String firstName, String lastName, String salutationName,
			String emailId, String questionAnswers, String patientId, LanguageOption preferredLanguage) {

		/* Replace the variables in the EmailBody */
		final String replaceStringWith[] = { hostname, firstName, lastName, salutationName, questionAnswers,
				patientId };
		String replaceSubjectStringWith[] = { firstName, lastName };
		final Locale locale = preferredLanguage.getLocale();

		String htmlBody = messageSource.getMessage(EmailConstants.CRC_PATIENT_WITHDRAW_BODY, replaceStringWith, locale);
		String subject = messageSource.getMessage(EmailConstants.CRC_PATIENT_WITHDRAW_SUBJECT, replaceSubjectStringWith,
				locale);

		return sendEmailNotification(emailId, senderEmailAddress, subject, htmlBody);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToParticipantReminderUnreadReport(String recipientEmail, String userFirstName,
			LanguageOption preferredLanguage) {
		String replaceStringWith[] = { hostname, userFirstName };

		return this.sendEmailAndLogStatus(recipientEmail, EmailConstants.PATIENT_REMINDER_REPORT_BODY,
				EmailConstants.PATIENT_REMINDER_REPORT_SUBJECT, EmailConstants.PARTICIPATING_SIGNATURE,
				replaceStringWith, preferredLanguage);
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToCRCAndProvidersReminderUnreadReport(String salutationFirstName, String recipientEmail,
			String patientFullName, LanguageOption preferredLanguage) {
		String replaceStringWith[] = { hostname, salutationFirstName, patientFullName };

		return this.sendEmailAndLogStatus(recipientEmail, EmailConstants.CRC_PROVIDER_REMINDER_REPORT_BODY,
				EmailConstants.CRC_PROVIDER_REMINDER_REPORT_SUBJECT, EmailConstants.CONTRIBUTING_SIGNATURE,
				replaceStringWith, preferredLanguage);
	}
}