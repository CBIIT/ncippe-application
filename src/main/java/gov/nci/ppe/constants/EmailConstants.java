package gov.nci.ppe.constants;

public class EmailConstants {

	// Send email to invite patients.
	public static final String PATIENT_INVITE_PORTAL_SUBJECT = "email.patient.invite.portal.subject";
	public static final String PATIENT_INVITE_PORTAL_BODY = "email.patient.invite.portal.body";

	public static final String PROVIDER_PATIENT_ADDED_SUBJECT = "email.provider.patient.invite.portal.subject";
	public static final String PROVIDER_PATIENT_ADDED_BODY = "email.provider.patient.invite.portal.body";

	// Add new Patient from OPEN - email CRC
	public static final String CRC_PATIENT_ADDED_FROM_OPEN_SUBJECT = "email.crc.new.patient.from.open.subject";
	public static final String CRC_PATIENT_ADDED_FROM_OPEN_BODY = "email.crc.new.patient.from.open.body";

	// Send Email after Uploading Biomarker report
	// To Patient
	public static final String PATIENT_UPLOAD_REPORT_SUBJECT = "email.uploadReport.patient.subject";
	public static final String PATIENT_UPLOAD_REPORT_BODY = "email.uploadReport.patient.body";

	// To LabAdmin
	public static final String LAB_ADMIN_UPLOAD_REPORT_SUBJECT = "email.uploadFile.admin.subject";
	public static final String LAB_ADMIN_UPLOAD_REPORT_BODY = "email.uploadFile.admin.body";
	
	//To CRC and Provider
	public static final String CRC_PROVIDER_UPLOAD_REPORT_SUBJECT = "email.crc.provider.upload.biomarker.report.subject";
	public static final String CRC_PROVIDER_UPLOAD_REPORT_BODY = "email.crc.provider.upload.biomarker.report.body";
	
	// Invite new Users - CRC and Providers - from OPEN
	public static final String CRC_PROVIDER_ADD_FROM_OPEN_SUBJECT = "email.nonpatient.invite.portal.subject";
	public static final String CRC_PROVIDER_ADD_FROM_OPEN_BODY = "email.nonpatient.invite.portal.body";

	// Invite new Users - CRC and Providers - from OPEN
	public static final String CRC_PATIENT_ADDED_SUBJECT = "email.crc.when.patients.added.subject";
	public static final String CRC_PATIENT_ADDED_BODY = "email.crc.when.patients.added.body";

	// Upload eConsent - send to Patient
	public static final String PATIENT_UPLOAD_ECONSENT_SUBJECT = "email.uploadFile.eConsentForm.subject";
	public static final String PATIENT_UPLOAD_ECONSENT_BODY = "email.uploadFile.eConsentForm.body";

	// Email CRC when Patient withdraws
	public static final String CRC_PATIENT_WITHDRAW_SUBJECT = "email.crc.withdraw.participation.subject";
	public static final String CRC_PATIENT_WITHDRAW_BODY = "email.crc.withdraw.participation.body";

	public static final String PATIENT_CRC_WITHDRAW_SUBJECT = "email.patient.withdraw.participation.subject";
	public static final String PATIENT_CRC_WITHDRAW_BODY = "email.patient.withdraw.participation.body";

	// Reminder to Patient Unread Report
	public static final String PATIENT_REMINDER_REPORT_SUBJECT = "email.reminder.unread.report.patient.subject";
	public static final String PATIENT_REMINDER_REPORT_BODY = "email.reminder.unread.report.patient.body";

	// Reminder to Patient Unread Report
	public static final String CRC_PROVIDER_REMINDER_REPORT_SUBJECT = "email.crc.provider.reminder.unread.biomarker.report.subject";
	public static final String CRC_PROVIDER_REMINDER_REPORT_BODY = "email.crc.provider.reminder.unread.biomarker.report.body";

	public static final String PATIENT_CHANGE_PROVIDER_SUBJECT = "email.patient.when.provider.changes.subject";
	public static final String PATIENT_CHANGE_PROVIDER_BODY = "email.patient.when.provider.changes.body";

	public static final String PATIENT_CHANGE_CRC_SUBJECT = "email.patient.when.crc.changes.subject";
	public static final String PATIENT_CHANGE_CRC_BODY = "email.patient.when.crc.changes.body";

	// Signatures
	public static final String JOINING_SIGNATURE = "email.joining.signature";
	public static final String PARTICIPATING_SIGNATURE = "email.thankyou.participating.signature";
	public static final String CONTRIBUTING_SIGNATURE = "email.thankyou.contribution.signature";
}
