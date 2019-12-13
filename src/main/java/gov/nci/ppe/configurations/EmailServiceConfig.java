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

	@Value("${email.common.signature}")
	private String commonSignature;

	@Value("${email.joining.signature}")
	private String joiningSignature;

	@Value("${email.thankyou.signature}")
	private String thankYouSignature;
	/*
	 * The below properties are related to the action - sending email to participant
	 * on uploading test report
	 */
	@Value("${email.uploadReport.patient.subject}")
	private String emailUploadReportPatientSubject;

	@Value("${email.uploadReport.patient.textBody}")
	private String emailUploadReportPatientTextBody;

	@Value("${email.uploadReport.patient.htmlBody}")
	private String emailUploadReportPatientHTMLBody;

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
	 * The below properties are related to the action - sending email to Mocha Admin
	 * on uploading test report
	 */
	@Value("${email.uploadFile.adminSubject}")
	private String emailSubjectForAdmin;

	@Value("${email.uploadFile.adminTextBody}")
	private String emailTextBodyForAdmin;

	@Value("${email.uploadFile.adminHtmlBody}")
	private String emailHtmlBodyForAdmin;

	/*
	 * The below properties are related to the action - sending email confirmation
	 * to CRC when patient withdraws participation
	 */
	@Value("${email.crc.withdraw.participation.subject}")
	private String emailSubjectForCRCWhenPatientWithdraws;

	@Value("${email.crc.withdraw.participation.textBody}")
	private String emailTextBodyForCRCWhenPatientWithdraws;

	@Value("${email.crc.withdraw.participation.htmlBody}")
	private String emailHtmlBodyForCRCWhenPatientWithdraws;

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

	/*
	 * Properties for sending Email notification to CRC and Providers when biomarker
	 * report is uploaded into the system by Mocha Admin for Participant
	 */
	@Value("${email.crc.provider.upload.biomarker.report.subject}")
	private String emailCRCAndProvidersAboutUNewlyUploadedBiomarkerReportSubject;

	@Value("${email.crc.provider.upload.biomarker.report.textBody}")
	private String emailCRCAndProvidersAboutUNewlyUploadedBiomarkerReportTextBody;

	@Value("${email.crc.provider.upload.biomarker.report.htmlBody}")
	private String emailCRCAndProvidersAboutUNewlypUloadedBiomarkerReportHtmlBody;
	
	/*
	 * Properties for sending Email notification to Participants when their
	 * providers change
	 */
	@Value("${email.patient.when.provider.changes.subject}")
	private String emailPatientWhenProvidersAreReplacedSubject;
	
	@Value("${email.patient.when.provider.changes.textBody}")
	private String emailPatientWhenProvidersAreReplacedTextBody;
	
	@Value("${email.patient.when.provider.changes.htmlBody}")
	private String emailPatientWhenProvidersAreReplacedHtmlBody;
	
	/*
	 * Properties for sending Email notification to Participants when their
	 * CRC change
	 */
	@Value("${email.patient.when.crc.changes.subject}")
	private String emailPatientWhenCRCIsReplacedSubject;
	
	@Value("${email.patient.when.crc.changes.textBody}")
	private String emailPatientWhenCRCIsReplacedTextBody;
	
	@Value("${email.patient.when.crc.changes.htmlBody}")
	private String emailPatientWhenCRCIsReplacedHtmlBody;

	/* Properties for sending Email to CRC when a patient is added */
	@Value("${email.crc.when.patients.added.subject}")
	private String emailCRCWhenPatientIsAddedSubject;
	
	@Value("${email.crc.when.patients.added.textBody}")
	private String emailCRCWhenPatientIsAddedTextBody;
	
	@Value("${email.crc.when.patients.added.htmlBody}")
	private String emailCRCWhenPatientIsAddedHtmlBody;

	/* Properties for sending Email to CRC when a patient is added */
	@Value("${email.provider.when.patients.added.subject}")
	private String emailProviderWhenPatientIsAddedSubject;
	
	@Value("${email.provider.when.patients.added.textBody}")
	private String emailProviderWhenPatientIsAddedTextBody;
	
	@Value("${email.provider.when.patients.added.htmlBody}")
	private String emailProviderWhenPatientIsAddedHtmlBody;
	
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

	public String getEmailUploadReportPatientSubject() {
		return emailUploadReportPatientSubject;
	}

	public String getEmailUploadReportPatientTextBody() {
		return emailUploadReportPatientTextBody;
	}

	public String getEmailUploadReportPatientHTMLBody() {
		return emailUploadReportPatientHTMLBody;
	}

	public String getCommonSignature() {
		return commonSignature;
	}

	public String getJoiningSignature() {
		return joiningSignature;
	}

	/**
	 * @return the emailCRCAndProvidersAboutUNewlyUploadedBiomarkerReportSubject
	 */
	public String getEmailCRCAndProvidersAboutUNewlyUploadedBiomarkerReportSubject() {
		return emailCRCAndProvidersAboutUNewlyUploadedBiomarkerReportSubject;
	}

	/**
	 * @return the emailCRCAndProvidersAboutUNewlyUploadedBiomarkerReportTextBody
	 */
	public String getEmailCRCAndProvidersAboutUNewlyUploadedBiomarkerReportTextBody() {
		return emailCRCAndProvidersAboutUNewlyUploadedBiomarkerReportTextBody;
	}

	/**
	 * @return the emailCRCAndProvidersAboutUNewlypUloadedBiomarkerReportHtmlBody
	 */
	public String getEmailCRCAndProvidersAboutUNewlypUloadedBiomarkerReportHtmlBody() {
		return emailCRCAndProvidersAboutUNewlypUloadedBiomarkerReportHtmlBody;
	}

	/**
	 * @return the emailPatientWhenProvidersAreReplacedSubject
	 */
	public String getEmailPatientWhenProvidersAreReplacedSubject() {
		return emailPatientWhenProvidersAreReplacedSubject;
	}

	/**
	 * @return the emailPatientWhenProvidersAreReplacedTextBody
	 */
	public String getEmailPatientWhenProvidersAreReplacedTextBody() {
		return emailPatientWhenProvidersAreReplacedTextBody;
	}

	/**
	 * @return the emailPatientWhenProvidersAreReplacedHtmlBody
	 */
	public String getEmailPatientWhenProvidersAreReplacedHtmlBody() {
		return emailPatientWhenProvidersAreReplacedHtmlBody;
	}

	/**
	 * @return the emailPatientWhenCRCIsReplacedSubject
	 */
	public String getEmailPatientWhenCRCIsReplacedSubject() {
		return emailPatientWhenCRCIsReplacedSubject;
	}

	/**
	 * @return the emailPatientWhenCRCIsReplacedTextBody
	 */
	public String getEmailPatientWhenCRCIsReplacedTextBody() {
		return emailPatientWhenCRCIsReplacedTextBody;
	}

	/**
	 * @return the emailPatientWhenCRCIsReplacedHtmlBody
	 */
	public String getEmailPatientWhenCRCIsReplacedHtmlBody() {
		return emailPatientWhenCRCIsReplacedHtmlBody;
	}

	/**
	 * @return the thankYouSignature
	 */
	public String getThankYouSignature() {
		return thankYouSignature;
	}

	/**
	 * @return the emailCRCWhenPatientIsAddedSubject
	 */
	public String getEmailCRCWhenPatientIsAddedSubject() {
		return emailCRCWhenPatientIsAddedSubject;
	}

	/**
	 * @return the emailCRCWhenPatientIsAddedTextBody
	 */
	public String getEmailCRCWhenPatientIsAddedTextBody() {
		return emailCRCWhenPatientIsAddedTextBody;
	}

	/**
	 * @return the emailCRCWhenPatientIsAddedHtmlBody
	 */
	public String getEmailCRCWhenPatientIsAddedHtmlBody() {
		return emailCRCWhenPatientIsAddedHtmlBody;
	}

	/**
	 * @return the emailProviderWhenPatientIsAddedSubject
	 */
	public String getEmailProviderWhenPatientIsAddedSubject() {
		return emailProviderWhenPatientIsAddedSubject;
	}

	/**
	 * @return the emailProviderWhenPatientIsAddedTextBody
	 */
	public String getEmailProviderWhenPatientIsAddedTextBody() {
		return emailProviderWhenPatientIsAddedTextBody;
	}

	/**
	 * @return the emailProviderWhenPatientIsAddedHtmlBody
	 */
	public String getEmailProviderWhenPatientIsAddedHtmlBody() {
		return emailProviderWhenPatientIsAddedHtmlBody;
	}
}