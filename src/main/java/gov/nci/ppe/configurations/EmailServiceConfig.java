package gov.nci.ppe.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * This is a configuration class that fetches all the required values from
 * EmailService.properties in the classpath.
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-15
 */

@Configuration
@PropertySource("classpath:EmailService.properties")
public class EmailServiceConfig {

	@Value("${sender.email.address}")
	private String senderEmailAddress;

	@Value("${email.use.aws}")
	private boolean useAWSSES;

	/*
	 * The below properties are related to the action - upload test report for
	 * participant
	 */
	@Value("${email.uploadFile.subject}")
	private String emailUploadFileSubject;

	@Value("${email.uploadFile.textBody}")
	private String emailUploadFileTextBody;

	@Value("${email.uploadFile.htmlBody}")
	private String emailUploadFileHTMLBody;
	/*
	 * The above properties are related to the action - upload test report for
	 * participant
	 */

	/*
	 * The below properties are related to the action - upload eConsent Form for
	 * participant
	 */
	@Value("${email.uploadFile.eConsentForm.subject}")
	private String emailSubjectForEConsent;

	@Value("${email.uploadFile.eConsentForm.textBody}")
	private String emailBodyInTextFormatForEConsent;

	@Value("${email.uploadFile.eConsentForm.htmlBody}")
	private String emailBodyInHtmlFormatForEConsent;
	/*
	 * The above properties are related to the action - upload eConsent Form for
	 * participant
	 */

	/*
	 * The below properties are related to the action - Email confirmation to Admin
	 * who uploads biomarker report
	 */
	@Value("${email.uploadFile.adminSubject}")
	private String emailSubjectForAdmin;

	@Value("${email.uploadFile.adminTextBody}")
	private String emailTextBodyForAdmin;

	@Value("${email.uploadFile.adminHtmlBody}")
	private String emailHtmlBodyForAdmin;
	/*
	 * The above properties are related to the action - Email confirmation to Admin
	 * who uploads biomarker report
	 */

	/*
	 * The below properties are related to the action - Email confirmation to CRC
	 * when patient withdraws participation
	 */
	@Value("${email.crc.withdraw.participation.subject}")
	private String emailSubjectForCRCWhenPatientWithdraws;

	@Value("${email.crc.withdraw.participation.textBody}")
	private String emailTextBodyForCRCWhenPatientWithdraws;

	@Value("${email.crc.withdraw.participation.htmlBody}")
	private String emailHtmlBodyForCRCWhenPatientWithdraws;
	/*
	 * The above properties are related to the action - Email confirmation to CRC
	 * when patient withdraws participation
	 */

	/*
	 * The below properties are related to the action - Email confirmation to
	 * Patient when CRC withdraws a particular patient's participation
	 */
	@Value("${email.patient.withdraw.participation.subject}")
	private String emailSubjectForPatientWhenCRCWithdraws;

	@Value("${email.patient.withdraw.participation.textBody}")
	private String emailTextBodyForPatientWhenCRCWithdraws;

	@Value("${email.patient.withdraw.participation.htmlBody}")
	private String emailHtmlBodyForPatientWhenCRCWithdraws;
	/*
	 * The above properties are related to the action - Email confirmation to
	 * Patient when CRC withdraws a particular patient's participation
	 */

	/* Properties for inviting a new patient to participate in the Portal */
	@Value("${email.patient.invite.portal.subject}")
	private String emailSubjectForPatientInvite;

	@Value("${email.patient.invite.portal.textBody}")
	private String emailTextBodyForPatientInvite;

	@Value("${email.patient.invite.portal.htmlBody}")
	private String emailHtmlBodyForPatientInvite;

	@Value("${email.provider.patient.invite.portal.subject}")
	private String emailSubjectForProviderPatientInvite;

	@Value("${email.provider.patient.invite.portal.textBody}")
	private String emailTextBodyForProviderPatientInvite;

	/*
	 * Properties for inviting a new HCP, CRC, BSSC and Admin to participate in the
	 * Portal
	 */
	@Value("${email.nonpatient.invite.portal.subject}")
	private String emailSubjectForNonPatientInvite;

	@Value("${email.nonpatient.invite.portal.textBody}")
	private String emailTextBodyForNonPatientInvite;

	@Value("${email.nonpatient.invite.portal.htmlBody}")
	private String emailHtmlBodyForNonPatientInvite;

	@Value("${email.crc.new.patient.from.open.subject}")
	private String emailCRCAboutNewPatientDataFromOpenSubject;

	@Value("${email.crc.new.patient.from.open.textBody}")
	private String emailCRCAboutNewPatientDataFromOpenTextBody;

	@Value("${email.crc.new.patient.from.open.htmlBody}")
	private String emailCRCAboutNewPatientDataFromOpenHtmlBody;

	public String getEmailSubjectForProviderPatientInvite() {
		return emailSubjectForProviderPatientInvite;
	}

	public String getEmailTextBodyForProviderPatientInvite() {
		return emailTextBodyForProviderPatientInvite;
	}

	public String getEmailHtmlBodyForProviderPatientInvite() {
		return emailHtmlBodyForProviderPatientInvite;
	}

	@Value("${email.provider.patient.invite.portal.htmlBody}")
	private String emailHtmlBodyForProviderPatientInvite;

	public String getSenderEmailAddress() {
		return senderEmailAddress;
	}

	public String getEmailUploadFileSubject() {
		return emailUploadFileSubject;
	}

	public String getEmailUploadFileTextBody() {
		return emailUploadFileTextBody;
	}

	public String getEmailUploadFileHTMLBody() {
		return emailUploadFileHTMLBody;
	}

	/**
	 * @return the emailSubjectForEConsent
	 */
	public String getEmailSubjectForEConsent() {
		return emailSubjectForEConsent;
	}

	/**
	 * @return the emailBodyInTextFormatForEConsent
	 */
	public String getEmailBodyInTextFormatForEConsent() {
		return emailBodyInTextFormatForEConsent;
	}

	/**
	 * @return the emailBodyInHtmlFormatForEConsent
	 */
	public String getEmailBodyInHtmlFormatForEConsent() {
		return emailBodyInHtmlFormatForEConsent;
	}

	/**
	 * @return the emailSubjectForAdmin
	 */
	public String getEmailSubjectForAdmin() {
		return emailSubjectForAdmin;
	}

	/**
	 * @return the emailTextBodyForAdmin
	 */
	public String getEmailTextBodyForAdmin() {
		return emailTextBodyForAdmin;
	}

	/**
	 * @return the emailHtmlBodyForAdmin
	 */
	public String getEmailHtmlBodyForAdmin() {
		return emailHtmlBodyForAdmin;
	}

	/**
	 * @return the emailSubjectForCRCWhenPatientWithdraws
	 */
	public String getEmailSubjectForCRCWhenPatientWithdraws() {
		return emailSubjectForCRCWhenPatientWithdraws;
	}

	/**
	 * @return the emailTextBodyForCRCWhenPatientWithdraws
	 */
	public String getEmailTextBodyForCRCWhenPatientWithdraws() {
		return emailTextBodyForCRCWhenPatientWithdraws;
	}

	/**
	 * @return the emailHtmlBodyForCRCWhenPatientWithdraws
	 */
	public String getEmailHtmlBodyForCRCWhenPatientWithdraws() {
		return emailHtmlBodyForCRCWhenPatientWithdraws;
	}

	/**
	 * @return the emailSubjectForPatientWhenCRCWithdraws
	 */
	public String getEmailSubjectForPatientWhenCRCWithdraws() {
		return emailSubjectForPatientWhenCRCWithdraws;
	}

	/**
	 * @return the emailTextBodyForPatientWhenCRCWithdraws
	 */
	public String getEmailTextBodyForPatientWhenCRCWithdraws() {
		return emailTextBodyForPatientWhenCRCWithdraws;
	}

	/**
	 * @return the emailHtmlBodyForPatientWhenCRCWithdraws
	 */
	public String getEmailHtmlBodyForPatientWhenCRCWithdraws() {
		return emailHtmlBodyForPatientWhenCRCWithdraws;
	}

	public String getEmailSubjectForPatientInvite() {
		return emailSubjectForPatientInvite;
	}

	public String getEmailTextBodyForPatientInvite() {
		return emailTextBodyForPatientInvite;
	}

	public String getEmailHtmlBodyForPatientInvite() {
		return emailHtmlBodyForPatientInvite;
	}

	/**
	 * @return the emailSubjectForNonPatientInvite
	 */
	public String getEmailSubjectForNonPatientInvite() {
		return emailSubjectForNonPatientInvite;
	}

	/**
	 * @return the emailTextBodyForNonPatientInvite
	 */
	public String getEmailTextBodyForNonPatientInvite() {
		return emailTextBodyForNonPatientInvite;
	}

	/**
	 * @return the emailHtmlBodyForNonPatientInvite
	 */
	public String getEmailHtmlBodyForNonPatientInvite() {
		return emailHtmlBodyForNonPatientInvite;
	}

	/**
	 * @return the emailCRCAboutNewPatientDataFromOpenSubject
	 */
	public String getEmailCRCAboutNewPatientDataFromOpenSubject() {
		return emailCRCAboutNewPatientDataFromOpenSubject;
	}

	/**
	 * @return the emailCRCAboutNewPatientDataFromOpenTextBody
	 */
	public String getEmailCRCAboutNewPatientDataFromOpenTextBody() {
		return emailCRCAboutNewPatientDataFromOpenTextBody;
	}

	/**
	 * @return the emailCRCAboutNewPatientDataFromOpenHtmlBody
	 */
	public String getEmailCRCAboutNewPatientDataFromOpenHtmlBody() {
		return emailCRCAboutNewPatientDataFromOpenHtmlBody;
	}

	public boolean getUseAWSSES() {
		return useAWSSES;
	}
}