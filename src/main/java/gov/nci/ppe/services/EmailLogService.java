package gov.nci.ppe.services;

import org.springframework.stereotype.Component;

import gov.nci.ppe.constants.CommonConstants.LanguageOption;
import gov.nci.ppe.data.entity.Participant;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-13
 */
@Component
public interface EmailLogService {

	/**
	 * Method to send an email to users of the portal
	 * 
	 * @param recipientEmail - Recipient's email address
	 * @param senderEmail    - Sender's email address
	 * @param subject        - Subject for the email
	 * @param htmlBody       - The text for the body (in HTML format) of the email
	 * 
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailNotification(String recipientEmail, String senderEmail, String subject, String htmlBody);

	/**
	 * Method to send an Invitation email to Patient
	 * 
	 * @param recipientEmail    - Recipient's email address
	 * @param patientFirstName  - Recipient's first name
	 * @param preferredLanguage - Recipient's preferred language
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToInvitePatient(String recipientEmail, String patientFirstName,
			LanguageOption preferredLanguage);

	/**
	 * Method to send a Notification email to Provider when their Patient is invited
	 * to join the BioBank, or when a patient is reassigned to them
	 * 
	 * @param recipientEmail    - Recipient's email address
	 * @param providerFirstName - Recipient's first name
	 * @param preferredLanguage - Recipient's preferred language
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToProviderOnPatientInvitation(String recipientEmail, String providerFirstName,
			LanguageOption preferredLanguage);

	/**
	 * Method to send an Invitation email to HCP, CRC, BSSC and Admins
	 * 
	 * @param recipientEmail    - Recipient's email address
	 * @param providerFirstName - Recipient's first name
	 * @param preferredLanguage - Recipient's preferred language
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToInviteNonPatients(String recipientEmail, String firstName,
			LanguageOption preferredLanguage);

	/**
	 * Method to send Email to associated CRC when a patient is imported from OPEN
	 * 
	 * @param recipientEmail    - Recipient's email address
	 * @param firstName         - Recipient's first name
	 * @param preferredLanguage - Recipient's preferred language
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToCRCOnNewPatient(String recipientEmail, String firstName, LanguageOption preferredLanguage);
	
	/**
	 * Method to send an email to CRC and Providers after Mocha Admin uploads a
	 * biomarker report for a participant.
	 * 
	 * @param salutationFirstName - First Name for CRC/Provider
	 * @param recipientEmail      - CRC/Provider's email id
	 * @param patientName         - Full Name of the patient for whom the biomarker
	 *                            report was uploaded
	 * @param preferredLanguage   - Recipient's preferred language
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToCRCAndProvidersAfterUploadingBioMarkerReport(String salutationFirstName, String recipientEmail, String patientName,
			LanguageOption preferredLanguage);

	/**
	 * Method to send an email to participant after their biomarker report is
	 * uploaded.
	 * 
	 * @param recipientEmail    - Recipient's email address
	 * @param userFirstName     - Recipient's first name
	 * @param preferredLanguage - Recipient's preferred language
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToPatientAfterUploadingReport(String recipientEmail, String userFirstName,
			LanguageOption preferredLanguage);

	/**
	 * Method to send Email to patient when eConsent form is uploaded
	 * 
	 * @param recipientEmail    - Recipient's email address
	 * @param firstName         - Recipient's first name
	 * @param preferredLanguage - Recipient's preferred language
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToPatientAfterUploadingEconsent(String recipientEmail, String firstName,
			LanguageOption preferredLanguage);
	
	/**
	 * Method to send out email to Mocha Admin when a file is uploaded biomarker
	 * report is uploaded by the admin
	 * 
	 * @param patient           - An object of Participant
	 * @param admin             - An object of User
	 * @param preferredLanguage - Recipient's preferred language
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToAdminAfterFileUpload(Participant participant, String emailId,
			LanguageOption preferredLanguage);

	/**
	 * Method to send an email to Patient when there is a change in their Provider
	 * 
	 * @param recipientEmail    - Recipient's email address
	 * @param patientFirstName  - Recipient's first name
	 * @param patientId         - Patient Id in the system
	 * @param preferredLanguage - Recipient's preferred language
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToPatientWhenProviderChanges(String recipientEmail, String patientFirstName,
			String patientId, LanguageOption preferredLanguage);

	/**
	 * Method to send an email to Patient when there is a change in their CRC
	 * 
	 * @param recipientEmail    - Recipient's email address
	 * @param patientFirstName  - Recipient's first name
	 * @param patientId         - Patient Id in the system
	 * @param preferredLanguage - Recipient's preferred language
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToPatientWhenCRCChanges(String recipientEmail, String patientirstName, String patientId,
			LanguageOption preferredLanguage);

	/**
	 * Method to send an email to CRC when a patient is included under them
	 * 
	 * @param recipientEmail    - Recipient's email address
	 * @param CRCFullName       - CRC's Full Name
	 * @param preferredLanguage - Recipient's preferred language
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToCRCWhenPatientIsAdded(String recipientEmail, String CRCFullName,
			LanguageOption preferredLanguage);

	/**
	 * Method to send out email to Patient when a CRC withdraws that participant
	 * from the PPE Program
	 * 
	 * @param firstName         - CRC's first name
	 * @param lastName          - CRC's last name
	 * @param salutationName    - Email Subscriber's first name
	 * @param emailId           - Email Subscriber's email id
	 * @param questionAnswers   - Survey question and answers taken by the CRC on
	 *                          behalf of the participant
	 * @param preferredLanguage - Recipient's preferred language
	 * @return - Success or Failure of the email process
	 */
	public String sendEmailToPatientAfterCRCWithdrawsPatient(String firstName, String lastName,
			String salutationName, String emailId, String questionAnswers, LanguageOption preferredLanguage);

	/**
	 * Method to send out email to CRC when an active participant withdraws self
	 * from the PPE Program
	 * 
	 * @param firstName       - Patient's First Name
	 * @param lastName        - Patient's Last Name
	 * @param salutationName  - Email Subscriber's first name
	 * @param emailId         - Email Subscriber's email id
	 * @param questionAnswers - Survey question and answers taken by the participant
	 * @param patientId       - Unique Id of the Patient
	 * @param preferredLanguage - Recipient's preferred language
	 * @return - Success or Failure of the email process
	 */
	public String sendEmailToCRCAfterParticipantWithdraws(String firstName, String lastName, String salutationName,
			String emailId, String questionAnswers, String patientId, LanguageOption preferredLanguage);

	/**
	 * Method to send a reminder email to participant if their biomarker report is
	 * unread
	 * 
	 * @param recipientEmail    - Recipient's email address
	 * @param userFirstName     - Recipient's first name
	 * @param preferredLanguage - Recipient's preferred language
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToParticipantReminderUnreadReport(String recipientEmail, String userFirstName,
			LanguageOption preferredLanguage);

	/**
	 * Method to send an email to CRC and Providers if they have unread biomarker
	 * reports for an associated participant.
	 * 
	 * @param salutationFirstName - First Name for CRC/Provider
	 * @param recipientEmail      - CRC/Provider's email id
	 * @param patientFullName     - Full name of Participant whose report is unread.
	 * @param preferredLanguage   - Recipient's preferred language
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToCRCAndProvidersReminderUnreadReport(String salutationFirstName, String recipientEmail,
			String patientFullName, LanguageOption preferredLanguage);

}