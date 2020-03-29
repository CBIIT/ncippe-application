package gov.nci.ppe.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;

/**
 * This is a configuration class that fetches all the required values from
 * NotificationService.properties in the classpath.
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-15
 */
@Getter
@Configuration
@PropertySource("classpath:NotificationService.properties")
public class NotificationServiceConfig {

	/* Notify participant when biomarker report is uploaded */
	@Value("${notify.participant.biomarker.report.upload.from}")
	private String uploadTestReportNotificationMessageFrom;

	@Value("${notify.participant.biomarker.report.upload.subject.en}")
	private String uploadTestReportNotificationMessageSubjectEnglish;

	@Value("${notify.participant.biomarker.report.upload.subject.es}")
	private String uploadTestReportNotificationMessageSubjectSpanish;

	@Value("${notify.participant.biomarker.report.upload.message.en}")
	private String uploadTestReportNotificationMessageEnglish;

	@Value("${notify.participant.biomarker.report.upload.message.es}")
	private String uploadTestReportNotificationMessageSpanish;

	/*
	 * Notify CRC and Providers when biomarker report is uploaded for a participant
	 */
	@Value("${notify.crc.provider.biomarker.report.upload.message.en}")
	private String notifyCRCProvidersBiomarkerReportUploadMessageEnglish;

	@Value("${notify.crc.provider.biomarker.report.upload.message.es}")
	private String notifyCRCProvidersBiomarkerReportUploadMessageSpanish;

	@Value("${notify.crc.provider.biomarker.report.upload.from}")
	private String notifyCRCProvidersBiomarkerReportUploadMessageFrom;

	@Value("${notify.crc.provider.biomarker.report.upload.subject.en}")
	private String notifyCRCProvidersBiomarkerReportUploadMessageSubjectEnglish;

	@Value("${notify.crc.provider.biomarker.report.upload.subject.es}")
	private String notifyCRCProvidersBiomarkerReportUploadMessageSubjectSpanish;

	/* Notify participant when eConsent form is uploaded */
	@Value("${eConsent.form.uploaded.notification.message.en}")
	private String uploadEConsentFormNotificationMessageEnglish;

	@Value("${eConsent.form.uploaded.notification.message.es}")
	private String uploadEConsentFormNotificationMessageSpanish;

	@Value("${eConsent.form.uploaded.notification.from}")
	private String uploadEConsentFormNotificationFrom;

	@Value("${eConsent.form.uploaded.notification.subject.en}")
	private String uploadEConsentFormNotificationSubjectEnglish;

	@Value("${eConsent.form.uploaded.notification.subject.es}")
	private String uploadEConsentFormNotificationSubjectSpanish;

	/*
	 * Notification properties for a CRC when Patient withdraws self from the
	 * program
	 */
	@Value("${participant.withdraws.program.subject.en}")
	private String participantWithdrawsSelfSubjectEnglish;

	@Value("${participant.withdraws.program.subject.es}")
	private String participantWithdrawsSelfSubjectSpanish;

	@Value("${participant.withdraws.program.message.en}")
	private String participantWithdrawsSelfMessageEnglish;

	@Value("${participant.withdraws.program.message.es}")
	private String participantWithdrawsSelfMessageSpanish;

	@Value("${participant.withdraws.program.from}")
	private String participantWithdrawsSelfFrom;

	/*
	 * Notification properties for a Patient when CRC withdraws a Patient from the
	 * program
	 */
	@Value("${crc.withdraws.participant.program.subject.en}")
	private String participantWithdrawnByCRCSubjectEnglish;

	@Value("${crc.withdraws.participant.program.subject.es}")
	private String participantWithdrawnByCRCSubjectSpanish;

	@Value("${crc.withdraws.participant.program.from}")
	private String participantWithdrawnByCRCFrom;

	@Value("${crc.withdraws.participant.program.message.en}")
	private String participantWithdrawnByCRCMessageEnglish;

	@Value("${crc.withdraws.participant.program.message.es}")
	private String participantWithdrawnByCRCMessageSpanish;

	@Value("${patient.receives.invitation.subject.en}")
	private String patientReceivesInvitationTitle;

	/* Notification properties for a provider when a CRC invites to Portal */
	@Value("${patient.receives.invitation.from}")
	private String patientReceivesInvitationFrom;

	@Value("${patient.receives.invitation.message.en}")
	private String patientReceivesInvitationMessageEnglish;

	@Value("${patient.receives.invitation.message.es}")
	private String patientReceivesInvitationMessageSpanish;

	/* Notify CRC when a new patient record from OPEN is inserted into PPE DB */
	@Value("${patient.added.from.open.subject.en}")
	private String patientAddedFromOpenSubjectEnglish;

	@Value("${patient.added.from.open.subject.es}")
	private String patientAddedFromOpenSubjectSpanish;

	@Value("${patient.added.from.open.from}")
	private String patientAddedFromOpenFrom;

	@Value("${patient.added.from.open.message.en}")
	private String patientAddedFromOpenMessageEnglish;

	@Value("${patient.added.from.open.message.es}")
	private String patientAddedFromOpenMessageSpanish;
	
	// Properties for notifying patients when Providers are replaced
	@Value("${provider.change.for.patient.subject.en}")
	private String notifyPatientWhenProvidersAreReplacedSubjectEnglish;

	@Value("${provider.change.for.patient.subject.es}")
	private String notifyPatientWhenProvidersAreReplacedSubjectSpanish;
	
	@Value("${provider.change.for.patient.from}")
	private String notifyPatientWhenProvidersAreReplacedFrom;
	
	@Value("${provider.change.for.patient.message.en}")
	private String notifyPatientWhenProvidersAreReplacedMessageEnglish;

	@Value("${provider.change.for.patient.message.es}")
	private String notifyPatientWhenProvidersAreReplacedMessageSpanish;
	
	// Properties for notifying patients when CRC is replaced
	@Value("${crc.change.for.patient.subject.en}")
	private String notifyPatientWhenCRCIsReplacedSubjectEnglish;
	
	@Value("${crc.change.for.patient.subject.es}")
	private String notifyPatientWhenCRCIsReplacedSubjectSpanish;

	@Value("${crc.change.for.patient.from}")
	private String notifyPatientWhenCRCIsReplacedFrom;
	
	@Value("${crc.change.for.patient.message.en}")
	private String notifyPatientWhenCRCIsReplacedMessageEnglish;

	@Value("${crc.change.for.patient.message.es}")
	private String notifyPatientWhenCRCIsReplacedMessageSpanish;
	
	// Properties for notifying Providers when a patient is added
	@Value("${notify.provider.when.patient.added.subject.en}")
	private String notifyProviderWhenPatientIsAddedSubjectEnglish;

	@Value("${notify.provider.when.patient.added.subject.es}")
	private String notifyProviderWhenPatientIsAddedSubjectSpanish;
	
	@Value("${notify.provider.when.patient.added.from}")
	private String notifyProviderWhenPatientIsAddedFrom;
	
	@Value("${notify.provider.when.patient.added.message.en}")
	private String notifyProviderWhenPatientIsAddedMessageEnglish;

	@Value("${notify.provider.when.patient.added.message.es}")
	private String notifyProviderWhenPatientIsAddedMessageSpanish;
	
	// Properties for notifying CRC when a patient is added	
	@Value("${notify.crc.when.patient.added.subject.en}")
	private String notifyCRCWhenPatientIsAddedSubjectEnglish;

	@Value("${notify.crc.when.patient.added.subject.es}")
	private String notifyCRCWhenPatientIsAddedSubjectSpanish;
	
	@Value("${notify.crc.when.patient.added.from}")
	private String notifyCRCWhenPatientIsAddedFrom;
	
	@Value("${notify.crc.when.patient.added.message.en}")
	private String notifyCRCWhenPatientIsAddedMessageEnglish;

	@Value("${notify.crc.when.patient.added.message.es}")
	private String notifyCRCWhenPatientIsAddedMessageSpanish;
	
	// Properties for reminding patient of unread biomarker report
	@Value("${reminder.unread.report.patient.from}")
	private String remindPatientUnreadReportFrom;

	@Value("reminder.unread.report.patient.subject.en}")
	private String remindPatientUnreadReportSubjectEnglish;

	@Value("${reminder.unread.report.patient.subject.es}")
	private String remindPatientUnreadReportSubjectSpanish;

	@Value("${reminder.unread.report.patient.message.en}")
	private String remindPatientUnreadReportMessageEnglish;
	
	@Value("${reminder.unread.report.patient.message.es}")
	private String remindPatientUnreadReportMessageSpanish;

// Remind CRC/Provider of unread biomarker report

	@Value("${reminder.unread.report.crc.provider.from}")
	private String remindCRCProviderUnreadReportFrom;

	@Value("${reminder.unread.report.crc.provider.subject.en}")
	private String remindCRCProviderUnreadReportSubjectEnglish;

	@Value("${reminder.unread.report.crc.provider.subject.es}")
	private String remindCRCProviderUnreadReportSubjectSpanish;

	@Value("${reminder.unread.report.crc.provider.message.en}")
	private String remindCRCProviderUnreadReportMessageEnglish;

	@Value("${reminder.unread.report.crc.provider.message.es}")
	private String remindCRCProviderUnreadReportMessageSpanish;

}