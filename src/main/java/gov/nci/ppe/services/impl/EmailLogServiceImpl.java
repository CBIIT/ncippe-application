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
import gov.nci.ppe.constants.CommonConstants.LanguageOption;
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
	public String sendEmailToInvitePatient(String recipientEmail, String patientFirstName,
			LanguageOption preferredLanguage) {
		String replaceStringWith[] = { patientFirstName };
		String replaceThisString[] = { "%{SalutationFirstName}" };

		String htmlBody;
		String subject;

		if ( useSpanish(preferredLanguage)) {
			htmlBody = emailServiceConfig.getEmailPatientInviteBodySpanish()
					+ emailServiceConfig.getJoiningSignatureSpanish();
			subject = emailServiceConfig.getEmailPatientInviteSubjectSpanish();
		} else {
			htmlBody = emailServiceConfig.getEmailPatientInviteBodyEnglish()
					+ emailServiceConfig.getJoiningSignatureEnglish();
			subject = emailServiceConfig.getEmailPatientInviteSubjectEnglish();
		}

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
	public String sendEmailToProviderOnPatientInvitation(String recipientEmail, String providerFirstName,
			LanguageOption preferredLanguage) {
		String replaceStringWith[] = { providerFirstName };
		String replaceThisString[] = { "%{SalutationFirstName}" };

		String htmlBody;
		String subject;

		if (useSpanish(preferredLanguage)) {
			htmlBody = emailServiceConfig.getEmailProviderPatientInviteBodySpanish()
					+ emailServiceConfig.getThankYouForContributionSignatureSpanish();
			subject = emailServiceConfig.getEmailProviderPatientInviteSubjectSpanish();
		} else {
			htmlBody = emailServiceConfig.getEmailProviderPatientInviteBodyEnglish()
					+ emailServiceConfig.getThankYouForContributionSignatureEnglish();
			subject = emailServiceConfig.getEmailProviderPatientInviteSubjectEnglish();
		}
		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith);
		String emailStatus = sendEmail(recipientEmail, subject, updatedHtmlBody, true);
		if (emailStatus.contains(CommonConstants.SUCCESS)) {
			logEmailStatus(recipientEmail, subject, updatedHtmlBody);
		}
		return emailStatus;

	}

	@Override
	public String sendEmailToCRCOnNewPatient(String recipientEmail, String firstName,
			LanguageOption preferredLanguage) {
		String replaceStringWith[] = { firstName };
		String replaceThisString[] = { "%{SalutationFirstName}" };

		String htmlBody;
		String subject;

		if ( useSpanish(preferredLanguage)) {
			htmlBody = emailServiceConfig.getEmailCRCAboutNewPatientDataFromOpenBodySpanish()
				+ emailServiceConfig.getThankYouForContributionSignatureSpanish();
			subject = emailServiceConfig.getEmailCRCAboutNewPatientDataFromOpenSubjectSpanish();
		} else {
			htmlBody = emailServiceConfig.getEmailCRCAboutNewPatientDataFromOpenBodyEnglish()
					+ emailServiceConfig.getThankYouForContributionSignatureEnglish();
			subject = emailServiceConfig.getEmailCRCAboutNewPatientDataFromOpenSubjectEnglish();

		}
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

		if (emailServiceConfig.isUseAWSSES()) {
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
	public String sendEmailToInviteNonPatients(String recipientEmail, String firstName,
			LanguageOption preferredLanguage) {
		String replaceStringWith[] = { firstName };
		String replaceThisString[] = { "%{SalutationFirstName}" };
		String htmlBody;
		String subject;

		if ( useSpanish(preferredLanguage)) {
			htmlBody = emailServiceConfig.getEmailNonPatientInviteBodySpanish()
					+ emailServiceConfig.getJoiningSignatureSpanish();
			subject = emailServiceConfig.getEmailNonPatientInviteSubjectSpanish();
		} else {
			htmlBody = emailServiceConfig.getEmailNonPatientInviteBodyEnglish()
					+ emailServiceConfig.getJoiningSignatureEnglish();
			subject = emailServiceConfig.getEmailNonPatientInviteSubjectEnglish();
		}
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
			String recipientEmail, String patientFullName, String patientId, LanguageOption preferredLanguage) {
		String replaceStringWith[] = { salutationFirstName, patientFullName, patientId };
		String replaceThisString[] = { "%{SalutationFirstName}", "%{FullName}", "%{PatientId}" };
		String htmlBody;
		String subject;

		if ( useSpanish(preferredLanguage)) {
			subject = emailServiceConfig.getEmailCRCAndProvidersAboutNewlyUploadedBiomarkerReportSubjectSpanish();
			htmlBody = emailServiceConfig.getEmailCRCAndProvidersAboutNewlyUploadedBiomarkerReportBodySpanish()
				+ emailServiceConfig.getThankYouForContributionSignatureSpanish();
		} else {
			subject = emailServiceConfig.getEmailCRCAndProvidersAboutNewlyUploadedBiomarkerReportSubjectSpanish();
			htmlBody = emailServiceConfig.getEmailCRCAndProvidersAboutNewlyUploadedBiomarkerReportBodySpanish()
					+ emailServiceConfig.getThankYouForContributionSignatureSpanish();
		}
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
	public String sendEmailToPatientAfterUploadingReport(String recipientEmail, String userFirstName,
			LanguageOption preferredLanguage) {
		String replaceStringWith[] = { userFirstName };
		String replaceThisString[] = { "%{SalutationFirstName}" };
		String htmlBody;
		String subject;
		String signature;

		if ( useSpanish(preferredLanguage)) {
			htmlBody = emailServiceConfig.getEmailUploadReportPatientBodySpanish();
			signature = emailServiceConfig.getThankYouParticipatingSignatureSpanish();
			subject = emailServiceConfig.getEmailUploadReportPatientSubjectSpanish();
		} else {
			htmlBody = emailServiceConfig.getEmailUploadReportPatientBodySpanish();
			signature = emailServiceConfig.getThankYouParticipatingSignatureSpanish();
			subject = emailServiceConfig.getEmailUploadReportPatientSubjectSpanish();

		}
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
	public String sendEmailToPatientAfterUploadingEconsent(String recipientEmail, String firstName,
			LanguageOption preferredLanguage) {
		String replaceStringWith[] = { firstName };
		String replaceThisString[] = { "%{SalutationFirstName}" };
		String htmlBody;
		String subject;
		String signature;

		if ( useSpanish(preferredLanguage)) {
			htmlBody = emailServiceConfig.getEmailUploadEConsentBodySpanish();
			subject = emailServiceConfig.getEmailUploadEConsentSubjectSpanish();
			signature = emailServiceConfig.getThankYouParticipatingSignatureSpanish();
		} else {
			htmlBody = emailServiceConfig.getEmailUploadEConsentBodySpanish();
			subject = emailServiceConfig.getEmailUploadEConsentSubjectSpanish();
			signature = emailServiceConfig.getThankYouParticipatingSignatureSpanish();
		}
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
	public String sendEmailToAdminAfterFileUpload(Participant participant, String recipientEmail,
			LanguageOption preferredLanguage) {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		dtf.format(now);
		String[] dateTime = StringUtils.split(dtf.format(now), "-");

		/* Replace the variables in the EmailBody */
		String replaceStringWith[] = { participant.getFirstName(), participant.getLastName(),
				participant.getPatientId(), dateTime[0], dateTime[1] };
		String replaceThisString[] = { "%{FirstName}", "%{LastName}", "%{UserGUID}", "%{date}", "%{time}" };
		String htmlBody;
		String subject;

		if ( useSpanish(preferredLanguage)) {
			htmlBody = emailServiceConfig.getEmailAdminBodySpanish();
			subject = emailServiceConfig.getEmailAdminSubjectSpanish();
		} else {
			htmlBody = emailServiceConfig.getEmailAdminBodyEnglish();
			subject = emailServiceConfig.getEmailAdminSubjectEnglish();
		}
		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith);

		/* Replace the variables in the Subject Line */
		String replaceSubjectStringWith[] = { participant.getPatientId() };
		String replaceThisStringForSubject[] = { "%{UserGUID}" };
		subject = StringUtils.replaceEach(subject, replaceThisStringForSubject, replaceSubjectStringWith);

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
	public String sendEmailToPatientWhenProviderChanges(String recipientEmail, String patientFirstName,
			String patientId, LanguageOption preferredLanguage) {
		String replaceStringWith[] = { patientFirstName , patientId };
		String replaceThisString[] = { "%{SalutationFirstName}", "%{PatientId}" };

		String htmlBody;
		String subject;
		String signature;

		if ( useSpanish(preferredLanguage)) {
			htmlBody = emailServiceConfig.getEmailPatientWhenProvidersAreReplacedBodySpanish();
			subject = emailServiceConfig.getEmailPatientWhenProvidersAreReplacedSubjectSpanish();
			signature = emailServiceConfig.getThankYouParticipatingSignatureSpanish();
		} else {
			htmlBody = emailServiceConfig.getEmailPatientWhenProvidersAreReplacedBodyEnglish();
			subject = emailServiceConfig.getEmailPatientWhenProvidersAreReplacedSubjectEnglish();
			signature = emailServiceConfig.getThankYouParticipatingSignatureEnglish();

		}
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
	public String sendEmailToPatientWhenCRCChanges(String recipientEmail, String patientirstName, String patientId,
			LanguageOption preferredLanguage) {
		String replaceStringWith[] = { patientirstName , patientId };
		String replaceThisString[] = { "%{SalutationFirstName}", "%{PatientId}" };

		String htmlBody;
		String subject;
		String signature;

		if ( useSpanish(preferredLanguage)) {
			htmlBody = emailServiceConfig.getEmailPatientWhenCRCIsReplacedBodySpanish();
			subject = emailServiceConfig.getEmailPatientWhenCRCIsReplacedSubjectSpanish();
			signature = emailServiceConfig.getThankYouParticipatingSignatureSpanish();
		} else {
			htmlBody = emailServiceConfig.getEmailPatientWhenCRCIsReplacedBodyEnglish();
			subject = emailServiceConfig.getEmailPatientWhenCRCIsReplacedSubjectEnglish();
			signature = emailServiceConfig.getThankYouParticipatingSignatureEnglish();
		}
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
	public String sendEmailToCRCWhenPatientIsAdded(String recipientEmail, String CRCFullName,
			LanguageOption preferredLanguage) {
		String replaceStringWith[] = { CRCFullName};
		String replaceThisString[] = { "%{SalutationFirstName}"};	
		String htmlBody;
		String subject;
		String signature;

		if ( useSpanish(preferredLanguage)) {
			subject = emailServiceConfig.getEmailCRCWhenPatientIsAddedSubjectSpanish();
			htmlBody = emailServiceConfig.getEmailCRCWhenPatientIsAddedBodySpanish();
			signature = emailServiceConfig.getThankYouForContributionSignatureSpanish();
		} else {
			subject = emailServiceConfig.getEmailCRCWhenPatientIsAddedSubjectEnglish();
			htmlBody = emailServiceConfig.getEmailCRCWhenPatientIsAddedBodyEnglish();
			signature = emailServiceConfig.getThankYouForContributionSignatureEnglish();
		}
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
	public String sendEmailToParticipantAfterCRCWithdrawsPatient(String firstName, String lastName,
			String salutationName, String emailId, String questionAnswers, LanguageOption preferredLanguage) {

		String htmlBody;
		String subject;
		String signature;

		if (useSpanish(preferredLanguage)) {
			htmlBody = emailServiceConfig.getEmailPatientWhenCRCWithdrawsBodySpanish();
			subject = emailServiceConfig.getEmailPatientWhenCRCWithdrawsSubjectSpanish();
			signature = emailServiceConfig.getThankYouForContributionSignatureSpanish();
		} else {
			htmlBody = emailServiceConfig.getEmailPatientWhenCRCWithdrawsBodyEnglish();
			subject = emailServiceConfig.getEmailPatientWhenCRCWithdrawsSubjectEnglish();
			signature = emailServiceConfig.getThankYouForContributionSignatureEnglish();
		}

		/* Replace the variables in the EmailBody */
		String replaceStringWith[] = { firstName, lastName, salutationName, questionAnswers };
		String replaceThisString[] = { "%{FirstName}", "%{LastName}", "%{SalutationFirstName}", "%{questionAnswer}" };

		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith);

		return sendEmailNotification(emailId, emailServiceConfig.getSenderEmailAddress(), subject,
				updatedHtmlBody + signature, updatedHtmlBody + signature);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String sendEmailToCRCAfterParticipantWithdraws(String firstName, String lastName, String salutationName,
			String emailId, String questionAnswers, String patientId, LanguageOption preferredLanguage) {
		String htmlBody;
		String subject;

		/* Replace the variables in the EmailBody */
		String replaceStringWith[] = { firstName, lastName, salutationName, questionAnswers, patientId };
		String replaceThisString[] = { "%{FirstName}", "%{LastName}", "%{SalutationFirstName}", "%{questionAnswer}",
				"%{PatientId}" };
		if (useSpanish(preferredLanguage)) {
			htmlBody = emailServiceConfig.getEmailCRCWhenPatientWithdrawsBodySpanish()
					+ emailServiceConfig.getThankYouForContributionSignatureSpanish();
			subject = emailServiceConfig.getEmailCRCWhenPatientWithdrawsSubjectSpanish();
		} else {
			htmlBody = emailServiceConfig.getEmailCRCWhenPatientWithdrawsBodyEnglish()
					+ emailServiceConfig.getThankYouForContributionSignatureEnglish();
			subject = emailServiceConfig.getEmailCRCWhenPatientWithdrawsSubjectEnglish();
		}
		String updatedHtmlBody = StringUtils.replaceEach(htmlBody, replaceThisString, replaceStringWith);

		/* Replace the variables in the Subject Line */
		String replaceSubjectStringWith[] = { firstName, lastName };
		String replaceThisStringForSubject[] = { "%{FirstName}", "%{LastName}" };
		subject = StringUtils.replaceEach(subject, replaceThisStringForSubject, replaceSubjectStringWith);
		return sendEmailNotification(emailId, emailServiceConfig.getSenderEmailAddress(), subject,
				updatedHtmlBody, updatedHtmlBody);
	}
	private boolean useSpanish(LanguageOption preferredLanguage) {
		return preferredLanguage != null && LanguageOption.SPANISH == preferredLanguage;
	}
}