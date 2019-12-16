package gov.nci.ppe.services.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.nci.ppe.configurations.NotificationServiceConfig;
import gov.nci.ppe.data.entity.PortalNotification;
import gov.nci.ppe.data.repository.PortalNotificationRepository;
import gov.nci.ppe.services.NotificationService;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-20
 */
@Component
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	private PortalNotificationRepository notificationRepo;

	@Autowired
	private NotificationServiceConfig notificationSrvConfig;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<PortalNotification> addNotification(String from, String subject, String message, Long userId,
			String userName, String patientName, String patientId) {

		/*
		 * The notification message has placeholders which needs to be replaced at
		 * runtime
		 */
		String replaceStringWith[] = { userName, patientName, patientId };
		String replaceThisString[] = { "%{FirstName}", "%{PatientName}", "%{PatientId}" };
		String updatedMessage = StringUtils.replaceEach(message, replaceThisString, replaceStringWith);
		String updatedSubject = StringUtils.replaceEach(subject, replaceThisString, replaceStringWith);
		return addNotificationToAccount(from, updatedSubject, updatedMessage, userId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PortalNotification> getAllNotificationsForUserByUserId(Long userId) {
		return notificationRepo.findNotificationstByUserId(userId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<PortalNotification> updateNotificationAsReadByNotificationId(PortalNotification notification) {
		return Optional.of(notificationRepo.save(notification));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<PortalNotification> getNotificationByNotificationId(Long notificationId) {
		return notificationRepo.findById(notificationId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PortalNotification> updateAllNotificationsForUserAsReadByUserGUID(
			List<PortalNotification> notificationsForUpdate) {
		return notificationRepo.saveAll(notificationsForUpdate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyPatientWhenCRCIsReplaced(Long userId) {
		addNotificationToAccount(notificationSrvConfig.getNotifyPatientWhenCRCIsReplacedFrom(),
				notificationSrvConfig.getNotifyPatientWhenCRCIsReplacedTitle(),
				notificationSrvConfig.getNotifyPatientWhenCRCIsReplacedMessage(), userId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyCRCWhenPatientIsAdded(String patientFullName, Long userId) {
		String replaceStringWith[] = { patientFullName };
		String replaceThisString[] = { "%{PatientFullName}" };
		String updatedMessage = StringUtils.replaceEach(notificationSrvConfig.getNotifyCRCWhenPatientIsAddedMessage(),
				replaceThisString, replaceStringWith);
		addNotificationToAccount(notificationSrvConfig.getNotifyCRCWhenPatientIsAddedFrom(),
				notificationSrvConfig.getNotifyCRCWhenPatientIsAddedTitle(), updatedMessage, userId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyPatientWhenProviderIsReplaced(Long userId) {
		addNotificationToAccount(notificationSrvConfig.getNotifyPatientWhenProvidersAreReplacedFrom(),
				notificationSrvConfig.getNotifyPatientWhenProvidersAreReplacedTitle(),
				notificationSrvConfig.getNotifyPatientWhenProvidersAreReplacedMessage(), userId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyProviderWhenPatientIsAdded(String patientFullName, Long userId) {
		String replaceStringWith[] = { patientFullName };
		String replaceThisString[] = { "%{PatientFullName}" };
		String updatedMessage = StringUtils.replaceEach(
				notificationSrvConfig.getNotifyProviderWhenPatientIsAddedMessage(), replaceThisString,
				replaceStringWith);
		addNotificationToAccount(notificationSrvConfig.getNotifyProviderWhenPatientIsAddedFrom(),
				notificationSrvConfig.getNotifyProviderWhenPatientIsAddedTitle(), updatedMessage, userId);
	}

	/**
	 * Private method that inserts a row into PortalNotification Table.
	 * 
	 * @param from    - The sender of the message. It is usually system notification
	 * @param title   - Subject of the message
	 * @param message - Content of the actual message
	 * @param userId  - Id for the recipient of the message
	 * @return
	 */
	private Optional<PortalNotification> addNotificationToAccount(String from, String title, String message,
			Long userId) {
		PortalNotification notificationObj = new PortalNotification();
		notificationObj.setMessageFrom(from);
		notificationObj.setSubject(title);
		notificationObj.setMessage(message);
		notificationObj.setUserId(userId);
		notificationObj.setDateGenerated(new Timestamp(System.currentTimeMillis()));
		notificationObj.setViewedByUser(0);
		notificationObj = notificationRepo.save(notificationObj);
		return Optional.of(notificationObj);
	}

}
