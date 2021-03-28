package gov.nci.ppe.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import gov.nci.ppe.constants.PPEUserType;
import gov.nci.ppe.data.entity.PortalNotification;
import gov.nci.ppe.data.entity.User;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-20
 */
@Component
public interface NotificationService {

	/**
	 * Insert a notification into the {PortalNotification} table.
	 * 
	 * @param messageFrom -
	 * @param subjectEnglish     - Subject/Title for the notification in English
	 * @param subjectSpanish     - Subject/Title for the notification in Spanish
	 * @param messageEnglish     - Message describing the notification in English
	 * @param messageSpanish     - Message describing the notification in Spanish
	 * @param userId      - user Id 
	 * @param userName    - First Name of the user to whom this notification is
	 *                    addressed
	 * @param patientName - Participant's first name.
	 * @return An Optional object of {PortalNotification} class
	 */
	public Optional<PortalNotification> addNotification(String messageFrom, String subjectEnglish,
			String subjectSpanish, String messageEnglish, String messageSpanish, Long userId,
			String userName, String patientName, String patientId);

	/**
	 * Fetch all the notification present in the table for a user
	 * 
	 * @param userId - User Id
	 * @return List of {PortalNotification} Objects
	 */
	public List<PortalNotification> getAllNotificationsForUserByUserId(Long userId);

	/**
	 * Mark the notification as {READ}
	 * 
	 * @param notification - Object of PortalNotification
	 * @return An Optional object of {PortalNotification} class
	 */
	public Optional<PortalNotification> updateNotificationAsReadByNotificationId(PortalNotification notification);

	/**
	 * Fetch notification based on notificationId
	 * 
	 * @param notificationId - Id for the notification
	 * @return An Optional object of {PortalNotification} class
	 */
	public Optional<PortalNotification> getNotificationByNotificationId(Long notificationId);

	/**
	 * Update all notifications for a particular user by marking them as {READ}
	 * 
	 * @param user - User whose notifications are to be marked as read
	 * @return List of {PortalNotification} Objects
	 */
	public List<PortalNotification> updateAllNotificationsForUserAsReadByUserGUID(
			User user);
	/**
	 * Method to add notify Patient when their CRC is replaced
	 * @param userId - Id of the recipient for whom the notification message is intended
	 */
	public void notifyPatientWhenCRCIsReplaced(Long userId);

	/**
	 * Method to notify CRC when a patient is assigned to them
	 * 
	 * @param patientFullName - Full name of the patient
	 * @param userId          - Id of the recipient for whom the notification
	 *                        message is intended
	 * @param patientId       - Patient ID of the patient.
	 */
	public void notifyCRCWhenPatientIsAdded(String patientFullName, Long userId, String patientId);

	/**
	 * Method to add notify Patient when their Provider is replaced
	 * @param userId - Id of the recipient for whom the notification message is intended
	 */
	public void notifyPatientWhenProviderIsReplaced(Long userId);

	/**
	 * Method to notify CRC when a patient is assigned to them
	 * 
	 * @param patientFullName - Full name of the patient
	 * @param userId          - Id of the recipient for whom the notification
	 *                        message is intended
	 * @param patientId       - Patient ID of the patient.
	 */
	public void notifyProviderWhenPatientIsAdded(String patientFullName, Long userId, String patientId);

	/**
	 * Method to remind Patient that they have an unread biomarker report
	 * 
	 * @param userId - UserId of the patient.
	 */
	public void notifyPatientReminderToReadBiomarkerReport(Long userId);

	/**
	 * Method to remind CRC/Provider that they have an unread biomarker report for
	 * one of their patients.
	 * 
	 * @param patientFullName - Full Name of the patient.
	 * @param userId          - Provider/CRC User Id
	 * @param patientId       - Patient ID of the patient.
	 */
	public void notifyProviderCRCReminderToReadBiomarkerReport(String patientFullName, Long userId, String patientId);

	/**
	 * Send Bulk notification to users by user type
	 * @param notification - Portal Notification to generate
	 * @param recipientGroups - List of user types to be generated
	 * @return - number of users notified
	 */
	public int sendGroupNotifications(PortalNotification notification, List<PPEUserType> recipientGroups);
}
