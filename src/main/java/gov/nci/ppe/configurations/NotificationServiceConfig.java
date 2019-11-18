package gov.nci.ppe.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * This is a configuration class that fetches all the required values from  NotificationService.properties in the classpath.
 * @author PublicisSapient
 * @version 1.0 
 * @since   2019-08-15
 */

@Configuration
@PropertySource("classpath:NotificationService.properties")
public class NotificationServiceConfig {
	
	@Value("${biomark.test.result.upload}")
	private String uploadTestReportNotificationMessage;

	@Value("${biomark.test.result.upload.from}")
	private String uploadTestReportNotificationMessageFrom;
	
	@Value("${biomark.test.result.upload.title}")
	private String uploadTestReportNotificationMessageSubject;
	
	@Value("${eConsent.form.uploaded.notification.message}")
	private String uploadEConsentFormNotificationMessage;
	
	@Value("${eConsent.form.uploaded.notification.from}")
	private String uploadEConsentFormNotificationFrom;
	
	@Value("${eConsent.form.uploaded.notification.title}")
	private String uploadEConsentFormNotificationSubject;
	
	@Value("${participant.withdraws.program.title}")
	private String participantWithdrawsSelfSubject;
	
	@Value("${participant.withdraws.program.message}")
	private String participantWithdrawsSelfMessage;
	
	@Value("${participant.withdraws.program.from}")
	private String participantWithdrawsSelfFrom;
	
	@Value("${crc.withdraws.participant.program.title}")
	private String participantWithdrawnByCRCSubject;
	
	@Value("${crc.withdraws.participant.program.from}")
	private String participantWithdrawnByCRCFrom;
	
	@Value("${crc.withdraws.participant.program.message}")
	private String participantWithdrawnByCRCMessage;
	
	@Value("${patient.receives.invitation.title}")
	private String patientReceivesInvitationTitle;

	@Value("${patient.receives.invitation.from}")
	private String patientReceivesInvitationFrom;

	@Value("${patient.receives.invitation.message}")
	private String patientReceivesInvitationMessage;
	
	@Value("${patient.added.from.open.title}")
	private String patientAddedFromOpenTitle;
	
	@Value("${patient.added.from.open.from}")
	private String patientAddedFromOpenFrom;
	
	@Value("${patient.added.from.open.message}")
	private String patientAddedFromOpenMessage;

	public String getUploadTestReportNotificationMessage() {
		return uploadTestReportNotificationMessage;
	}

	public String getUploadTestReportNotificationMessageFrom() {
		return uploadTestReportNotificationMessageFrom;
	}

	public String getUploadTestReportNotificationMessageSubject() {
		return uploadTestReportNotificationMessageSubject;
	}

	/**
	 * @return the uploadEConsentFormNotificationMessage
	 */
	public String getUploadEConsentFormNotificationMessage() {
		return uploadEConsentFormNotificationMessage;
	}

	/**
	 * @return the uploadEConsentFormNotificationFrom
	 */
	public String getUploadEConsentFormNotificationFrom() {
		return uploadEConsentFormNotificationFrom;
	}

	/**
	 * @return the uploadEConsentFormNotificationSubject
	 */
	public String getUploadEConsentFormNotificationSubject() {
		return uploadEConsentFormNotificationSubject;
	}

	/**
	 * @return the participantWithdrawsSelfSubject
	 */
	public String getParticipantWithdrawsSelfSubject() {
		return participantWithdrawsSelfSubject;
	}

	/**
	 * @return the participantWithdrawsSelfMessage
	 */
	public String getParticipantWithdrawsSelfMessage() {
		return participantWithdrawsSelfMessage;
	}

	/**
	 * @return the participantWithdrawsSelfFrom
	 */
	public String getParticipantWithdrawsSelfFrom() {
		return participantWithdrawsSelfFrom;
	}

	/**
	 * @return the participantWithdrawnByCRCSubject
	 */
	public String getParticipantWithdrawnByCRCSubject() {
		return participantWithdrawnByCRCSubject;
	}

	/**
	 * @return the participantWithdrawnByCRCMessage
	 */
	public String getParticipantWithdrawnByCRCMessage() {
		return participantWithdrawnByCRCMessage;
	}

	/**
	 * @return the participantWithdrawnByCRCFrom
	 */
	public String getParticipantWithdrawnByCRCFrom() {
		return participantWithdrawnByCRCFrom;
	}

	public String getPatientReceivesInvitationTitle() {
		return patientReceivesInvitationTitle;
	}

	public String getPatientReceivesInvitationFrom() {
		return patientReceivesInvitationFrom;
	}

	public String getPatientReceivesInvitationMessage() {
		return patientReceivesInvitationMessage;
	}

	/**
	 * @return the patientAddedFromOpenTitle
	 */
	public String getPatientAddedFromOpenTitle() {
		return patientAddedFromOpenTitle;
	}

	/**
	 * @return the patientAddedFromOpenFrom
	 */
	public String getPatientAddedFromOpenFrom() {
		return patientAddedFromOpenFrom;
	}

	/**
	 * @return the patientAddedFromOpenMessage
	 */
	public String getPatientAddedFromOpenMessage() {
		return patientAddedFromOpenMessage;
	}

}