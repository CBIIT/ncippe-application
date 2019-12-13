package gov.nci.ppe.services;

import org.springframework.stereotype.Component;

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
	 * @param textBody       - The text for the body of the email
	 * 
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailNotification(String recipientEmail, String senderEmail, String subject, String htmlBody,
			String textBody);

	/**
	 * Method to send an Invitation email to Patient
	 * 
	 * @param recipientEmail   - Recipient's email address
	 * @param patientFirstName - Recipient's first name
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToInvitePatient(String recipientEmail, String patientFirstName);

	/**
	 * Method to send a Notification email to Provider when their Patient is invited
	 * 
	 * @param recipientEmail    - Recipient's email address
	 * @param providerFirstName - Recipient's first name
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToProviderOnPatientInvitation(String recipientEmail, String providerFirstName);

	/**
	 * Method to send an Invitation email to HCP, CRC, BSSC and Admins
	 * 
	 * @param recipientEmail    - Recipient's email address
	 * @param providerFirstName - Recipient's first name
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToInviteNonPatients(String recipientEmail, String firstName);

	/**
	 * Method to send Email to associated CRC when a patient is imported from OPEN
	 * 
	 * @param recipientEmail - Recipient's email address
	 * @param firstName      - Recipient's first name
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToCRCOnNewPatient(String recipientEmail, String firstName);
	
	/**
	 * Method to send an email to CRC and Providers after Mocha Admin uploads a biomarker report for a participant.
	 * 
	 * @param salutationFirstName - First Name for CRC/Provider
	 * @param recipientEmail - CRC/Provider's email id
	 * @param patientName - Full Name of the patient for whom the biomarker report was uploaded
	 * @param patientId - Patient Id
	 * @return
	 */
	public String sendEmailToCRCAndProvidersAfterUploadingBioMarkerReport(String salutationFirstName, String recipientEmail, String patientName,
			String patientId);

	/**
	 * Method to send an email to participant after their biomarker report is
	 * uploaded.
	 * 
	 * @param recipientEmail - Recipient's email address
	 * @param userFirstName  - Recipient's first name
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToPatientAfterUploadingReport(String recipientEmail, String userFirstName);

	/**
	 * Method to send Email to patient when eConsent form is uploaded
	 * 
	 * @param recipientEmail - Recipient's email address
	 * @param firstName      - Recipient's first name
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToPatientAfterUploadingEconsent(String recipientEmail, String firstName);
	
	/**
	 * Method to send out email to Mocha Admin when a file is uploaded
	 * biomarker report is uploaded by the admin
	 * @param patient - An object of Participant
	 * @param admin   - An object of User
	 * @return
	 */
	public String sendEmailToAdminAfterFileUpload(Participant participant, String emailId);

	/**
	 * Method to send an email to Patient when there is a change in their Provider
	 * @param recipientEmail   - Recipient's email address 
	 * @param patientFirstName - Recipient's first name
	 * @param patientId        - Patient Id in the system
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToPatientWhenProviderChanges(String recipientEmail, String patientFirstName, String patientId);

	/**
	 * Method to send an email to Patient when there is a change in their CRC
	 * @param recipientEmail   - Recipient's email address 
	 * @param patientFirstName - Recipient's first name
	 * @param patientId        - Patient Id in the system
	 * @return Email Status if the email was sent out successfully
	 */
	public String sendEmailToPatientWhenCRCChanges(String recipientEmail, String patientirstName, String patientId);

	/**
	 * Method to send an email to CRC when a patient is included under them
	 * @param recipientEmail - Recipient's email address
	 * @param CRCFullName    - CRC's Full Name
	 * @return
	 */
	public String sendEmailToCRCWhenPatientIsAdded(String recipientEmail, String CRCFullName);

	/**
	 * Method to send an email to Provider when a patient is included under them
	 * @param recipientEmail - Recipient's email address
	 * @param CRCFullName    - Provider's Full Name
	 * @return
	 */	
	public String sendEmailToProviderWhenPatientIsAdded(String recipientEmail, String CRCFullName);

}