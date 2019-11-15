package gov.nci.ppe.services;

import org.springframework.stereotype.Component;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-13
 */
@Component
public interface EmailLogService {
	
	/**
	 * Method to send an email after participant's biomarker report is uploaded.
	 * 
	 * @param userFirstName  - Recipient's first name
	 * @param recipientEmail - Recipient's email address
	 * @param senderEmail    - Sender's email address
	 * @param subject        - Subject for the email
	 * @param htmlBody       - The text for the body (in HTML format) of the email
	 * @param textBody       - The text for the body of the email
	 */
	public String sendEmailAfterUploadingReport(String userFirstName, String recipientEmail, String patienttName,String senderEmail,String subject, String htmlBody, String textBody);

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
	 * Method to send an Invitation email to Patient
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

}