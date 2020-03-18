package gov.nci.ppe.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import gov.nci.ppe.data.entity.PortalNotification;

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
	 * @param subject     - Subject/Title for the notification
	 * @param subject     - Subject/Title for the notification
	 * @param message     - Message describing the notification
	 * @param message     - Message describing the notification
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
	 * @param notificationsForUpdate
	 * @return List of {PortalNotification} Objects
	 */
	public List<PortalNotification> updateAllNotificationsForUserAsReadByUserGUID(
			List<PortalNotification> notificationsForUpdate);
	/**
	 * Method to add notify Patient when their CRC is replaced
	 * @param userId - Id of the recipient for whom the notification message is intended
	 */
	public void notifyPatientWhenCRCIsReplaced(Long userId);

	/**
	 * Method to nofity CRC when a patient is assigned to them
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
	 * Method to nofity CRC when a patient is assigned to them
	 * 
	 * @param patientFullName - Full name of the patient
	 * @param userId          - Id of the recipient for whom the notification
	 *                        message is intended
	 * @param patientId       - Patient ID of the patient.
	 */
	public void notifyProviderWhenPatientIsAdded(String patientFullName, Long userId, String patientId);

}
