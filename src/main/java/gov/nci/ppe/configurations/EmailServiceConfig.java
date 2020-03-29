package gov.nci.ppe.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;

/**
 * This is a configuration class that fetches all the required values from
 * EmailService.properties in the classpath.
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-15
 */

@Getter
@Configuration
@PropertySource("classpath:EmailService.properties")
public class EmailServiceConfig {

	@Value("${sender.email.address}")
	private String senderEmailAddress;

	@Value("${email.use.aws}")
	private boolean useAWSSES;

	@Value("${email.joining.signature.en}")
	private String joiningSignatureEnglish;

	@Value("${email.joining.signature.es}")
	private String joiningSignatureSpanish;

	@Value("${email.thankyou.participating.signature.en}")
	private String thankYouParticipatingSignatureEnglish;

	@Value("${email.thankyou.participating.signature.es}")
	private String thankYouParticipatingSignatureSpanish;
	
	@Value("${email.thankyou.contribution.signature.en}")
	private String thankYouForContributionSignatureEnglish;

	@Value("${email.thankyou.contribution.signature.es}")
	private String thankYouForContributionSignatureSpanish;

	/*
	 * The below properties are related to the action - sending email to participant
	 * on uploading test report
	 */
	@Value("${email.uploadReport.patient.subject.en}")
	private String emailUploadReportPatientSubjectEnglish;

	@Value("${email.uploadReport.patient.subject.es}")
	private String emailUploadReportPatientSubjectSpanish;

	@Value("${email.uploadReport.patient.body.es}")
	private String emailUploadReportPatientBodySpanish;

	@Value("${email.uploadReport.patient.body.en}")
	private String emailUploadReportPatientBodyEnglish;

	/*
	 * The below properties are related to the action - upload eConsent Form for
	 * participant
	 */
	@Value("${email.uploadFile.eConsentForm.subject.en}")
	private String emailUploadEConsentSubjectEnglish;

	@Value("${email.uploadFile.eConsentForm.subject.es}")
	private String emailUploadEConsentSubjectSpanish;

	@Value("${email.uploadFile.eConsentForm.body.es}")
	private String emailUploadEConsentBodySpanish;

	@Value("${email.uploadFile.eConsentForm.body.en}")
	private String emailUploadEConsentBodyEnglish;

	/*
	 * The below properties are related to the action - sending email to Mocha Admin
	 * on uploading test report
	 */
	@Value("${email.uploadFile.admin.subject.en}")
	private String emailAdminSubjectEnglish;

	@Value("${email.uploadFile.admin.subject.es}")
	private String emailAdminSubjectSpanish;

	@Value("${email.uploadFile.admin.body.en}")
	private String emailAdminBodyEnglish;

	@Value("${email.uploadFile.admin.body.es}")
	private String emailAdminBodySpanish;

	/*
	 * The below properties are related to the action - sending email confirmation
	 * to CRC when patient withdraws participation
	 */
	@Value("${email.crc.withdraw.participation.subject.en}")
	private String emailCRCWhenPatientWithdrawsSubjectEnglish;

	@Value("${email.crc.withdraw.participation.subject.es}")
	private String emailCRCWhenPatientWithdrawsSubjectSpanish;

	@Value("${email.crc.withdraw.participation.body.es}")
	private String emailCRCWhenPatientWithdrawsBodySpanish;

	@Value("${email.crc.withdraw.participation.body.en}")
	private String emailCRCWhenPatientWithdrawsBodyEnglish;

	/*
	 * The below properties are related to the action - Email confirmation to
	 * Patient when CRC withdraws a particular patient's participation
	 */
	@Value("${email.patient.withdraw.participation.subject.en}")
	private String emailPatientWhenCRCWithdrawsSubjectEnglish;

	@Value("${email.patient.withdraw.participation.subject.es}")
	private String emailPatientWhenCRCWithdrawsSubjectSpanish;

	@Value("${email.patient.withdraw.participation.body.es}")
	private String emailPatientWhenCRCWithdrawsBodySpanish;

	@Value("${email.patient.withdraw.participation.body.en}")
	private String emailPatientWhenCRCWithdrawsBodyEnglish;

	/* Properties for inviting a new patient to participate in the Portal */
	@Value("${email.patient.invite.portal.subject.en}")
	private String emailPatientInviteSubjectEnglish;

	@Value("${email.patient.invite.portal.subject.es}")
	private String emailPatientInviteSubjectSpanish;

	@Value("${email.patient.invite.portal.body.es}")
	private String emailPatientInviteBodySpanish;

	@Value("${email.patient.invite.portal.body.en}")
	private String emailPatientInviteBodyEnglish;

	@Value("${email.provider.patient.invite.portal.subject.en}")
	private String emailProviderPatientInviteSubjectEnglish;

	@Value("${email.provider.patient.invite.portal.subject.es}")
	private String emailProviderPatientInviteSubjectSpanish;

	@Value("${email.provider.patient.invite.portal.body.es}")
	private String emailProviderPatientInviteBodySpanish;

	@Value("${email.provider.patient.invite.portal.body.en}")
	private String emailProviderPatientInviteBodyEnglish;

	/*
	 * Properties for inviting a new HCP, CRC, BSSC and Admin to participate in the
	 * Portal
	 */
	@Value("${email.nonpatient.invite.portal.subject.en}")
	private String emailNonPatientInviteSubjectEnglish;

	@Value("${email.nonpatient.invite.portal.subject.es}")
	private String emailNonPatientInviteSubjectSpanish;

	@Value("${email.nonpatient.invite.portal.body.es}")
	private String emailNonPatientInviteBodySpanish;

	@Value("${email.nonpatient.invite.portal.body.en}")
	private String emailNonPatientInviteBodyEnglish;

	@Value("${email.crc.new.patient.from.open.subject.en}")
	private String emailCRCAboutNewPatientDataFromOpenSubjectEnglish;

	@Value("${email.crc.new.patient.from.open.subject.es}")
	private String emailCRCAboutNewPatientDataFromOpenSubjectSpanish;

	@Value("${email.crc.new.patient.from.open.body.es}")
	private String emailCRCAboutNewPatientDataFromOpenBodySpanish;

	@Value("${email.crc.new.patient.from.open.body.en}")
	private String emailCRCAboutNewPatientDataFromOpenBodyEnglish;

	/*
	 * Properties for sending Email notification to CRC and Providers when biomarker
	 * report is uploaded into the system by Mocha Admin for Participant
	 */
	@Value("${email.crc.provider.upload.biomarker.report.subject.en}")
	private String emailCRCAndProvidersAboutNewlyUploadedBiomarkerReportSubjectEnglish;

	@Value("${email.crc.provider.upload.biomarker.report.subject.es}")
	private String emailCRCAndProvidersAboutNewlyUploadedBiomarkerReportSubjectSpanish;

	@Value("${email.crc.provider.upload.biomarker.report.body.es}")
	private String emailCRCAndProvidersAboutNewlyUploadedBiomarkerReportBodySpanish;

	@Value("${email.crc.provider.upload.biomarker.report.body.en}")
	private String emailCRCAndProvidersAboutNewlypUloadedBiomarkerReportBodyEnglish;
	
	/*
	 * Properties for sending Email notification to Participants when their
	 * providers change
	 */
	@Value("${email.patient.when.provider.changes.subject.en}")
	private String emailPatientWhenProvidersAreReplacedSubjectEnglish;

	@Value("${email.patient.when.provider.changes.subject.es}")
	private String emailPatientWhenProvidersAreReplacedSubjectSpanish;
	
	@Value("${email.patient.when.provider.changes.body.es}")
	private String emailPatientWhenProvidersAreReplacedBodySpanish;
	
	@Value("${email.patient.when.provider.changes.body.en}")
	private String emailPatientWhenProvidersAreReplacedBodyEnglish;
	
	/*
	 * Properties for sending Email notification to Participants when their
	 * CRC change
	 */
	@Value("${email.patient.when.crc.changes.subject.en}")
	private String emailPatientWhenCRCIsReplacedSubjectEnglish;

	@Value("${email.patient.when.crc.changes.subject.es}")
	private String emailPatientWhenCRCIsReplacedSubjectSpanish;
	
	@Value("${email.patient.when.crc.changes.body.es}")
	private String emailPatientWhenCRCIsReplacedBodySpanish;
	
	@Value("${email.patient.when.crc.changes.body.en}")
	private String emailPatientWhenCRCIsReplacedBodyEnglish;

	/* Properties for sending Email to CRC when a patient is added */
	@Value("${email.crc.when.patients.added.subject.en}")
	private String emailCRCWhenPatientIsAddedSubjectEnglish;

	@Value("${email.crc.when.patients.added.subject.es}")
	private String emailCRCWhenPatientIsAddedSubjectSpanish;
	
	@Value("${email.crc.when.patients.added.body.es}")
	private String emailCRCWhenPatientIsAddedBodySpanish;
	
	@Value("${email.crc.when.patients.added.body.en}")
	private String emailCRCWhenPatientIsAddedBodyEnglish;

	/* Reminder email to Patient on unread test report */
	
	@Value("${email.reminder.unread.report.patient.subject.es}")
	private String emailPatientReminderUnreadReportSubjectSpanish;

	@Value("${email.reminder.unread.report.patient.subject.en}")
	private String emailPatientReminderUnreadReportSubjectEnglish;

	@Value("${email.reminder.unread.report.patient.body.es}")
	private String emailPatientReminderUnreadReportBodySpanish;

	@Value("${email.reminder.unread.report.patient.body.en}")
	private String emailPatientReminderUnreadReportBodyEnglish;

	/* Reminder email to CRC and Provider on unread test report */
	@Value("${email.crc.provider.reminder.unread.biomarker.report.subject.en}")
	private String emailCRCProviderReminderUnreadReportSubjectEnglish;

	@Value("${email.crc.provider.reminder.unread.biomarker.report.subject.es}")
	private String emailCRCProviderReminderUnreadReportSubjectSpanish;

	@Value("${email.crc.provider.reminder.unread.biomarker.report.body.es}")
	private String emailCRCProviderReminderUnreadReportBodySpanish;

	@Value("${email.crc.provider.reminder.unread.biomarker.report.body.en}")
	private String emailCRCProviderReminderUnreadReportBodyEnglish;

}