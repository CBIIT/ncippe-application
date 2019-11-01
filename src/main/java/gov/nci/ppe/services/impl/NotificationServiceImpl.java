package gov.nci.ppe.services.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

		PortalNotification notificationObj = new PortalNotification();
		notificationObj.setMessageFrom(from);
		notificationObj.setSubject(subject);
		notificationObj.setMessage(updatedMessage);
		notificationObj.setUserId(userId);
		notificationObj.setDateGenerated(new Timestamp(System.currentTimeMillis()));
		notificationObj.setViewedByUser(0);
		notificationObj = notificationRepo.save(notificationObj);
		return Optional.of(notificationObj);
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

}
