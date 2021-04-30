package gov.nci.ppe.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nci.ppe.configurations.NotificationServiceConfig;
import gov.nci.ppe.constants.CommonConstants.LanguageOption;
import gov.nci.ppe.data.entity.PortalNotification;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.data.repository.PortalNotificationRepository;
import gov.nci.ppe.services.AuditService;
import gov.nci.ppe.services.EmailLogService;
import gov.nci.ppe.services.NotificationService;

/**
 * Implementation class for {@link NotificationService}
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-20
 */
@Service
public class NotificationServiceImpl implements NotificationService {

	private static final String PATIENT_FULL_NAME_PLACEHOLDER = "%{PatientFullName}";

	private static final String PATIENT_ID_PLACEHOLDER = "%{PatientId}";

	private static final Logger logger = LogManager.getLogger(NotificationServiceImpl.class);

	private PortalNotificationRepository notificationRepo;

	private NotificationServiceConfig notificationSrvConfig;

	private EmailLogService emailService;

	@Autowired
	public NotificationServiceImpl(PortalNotificationRepository notificationRepo,
			NotificationServiceConfig notificationSrvConfig, EmailLogService emailService, AuditService auditService) {
		this.notificationRepo = notificationRepo;
		this.notificationSrvConfig = notificationSrvConfig;
		this.emailService = emailService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<PortalNotification> addNotification(String from, String subjectEnglish, String subjectSpanish,
			String messageEnglish, String messageSpanish, Long userId, String userName, String patientName,
			String patientId) {

		/*
		 * The notification message has placeholders which needs to be replaced at
		 * runtime
		 */
		String replaceStringWith[] = { userName, patientName, patientId };
		String replaceThisString[] = { "%{FirstName}", "%{PatientName}", PATIENT_ID_PLACEHOLDER };
		String updatedMessageEnglish = StringUtils.replaceEach(messageEnglish, replaceThisString, replaceStringWith);
		String updatedSubjectEnglish = StringUtils.replaceEach(subjectEnglish, replaceThisString, replaceStringWith);
		String updatedMessageSpanish = StringUtils.replaceEach(messageSpanish, replaceThisString, replaceStringWith);
		String updatedSubjectSpanish = StringUtils.replaceEach(subjectSpanish, replaceThisString, replaceStringWith);
		return addNotificationToAccount(from, updatedSubjectEnglish, updatedSubjectSpanish, updatedMessageEnglish,
				updatedMessageSpanish, userId);
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
	public List<PortalNotification> updateAllNotificationsForUserAsReadByUserGUID(User user) {
		List<PortalNotification> notificationsForUpdate = getAllNotificationsForUserByUserId(user.getUserId());

		notificationsForUpdate.forEach(notification -> {
			notification.setViewedByUser(1);
		});
		return notificationRepo.saveAll(notificationsForUpdate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyPatientWhenCRCIsReplaced(Long userId) {
		addNotificationToAccount(notificationSrvConfig.getNotifyPatientWhenCRCIsReplacedFrom(),
				notificationSrvConfig.getNotifyPatientWhenCRCIsReplacedSubjectEnglish(),
				notificationSrvConfig.getNotifyPatientWhenCRCIsReplacedSubjectSpanish(),
				notificationSrvConfig.getNotifyPatientWhenCRCIsReplacedMessageEnglish(),
				notificationSrvConfig.getNotifyPatientWhenCRCIsReplacedMessageSpanish(), userId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyCRCWhenPatientIsAdded(String patientFullName, Long userId, String patientId) {
		String replaceStringWith[] = { patientFullName, patientId };
		String replaceThisString[] = { PATIENT_FULL_NAME_PLACEHOLDER, PATIENT_ID_PLACEHOLDER };
		String updatedMessageEnglish = StringUtils.replaceEach(
				notificationSrvConfig.getNotifyCRCWhenPatientIsAddedMessageEnglish(), replaceThisString,
				replaceStringWith);
		String updatedMessageSpanish = StringUtils.replaceEach(
				notificationSrvConfig.getNotifyCRCWhenPatientIsAddedMessageSpanish(), replaceThisString,
				replaceStringWith);
		addNotificationToAccount(notificationSrvConfig.getNotifyCRCWhenPatientIsAddedFrom(),
				notificationSrvConfig.getNotifyCRCWhenPatientIsAddedSubjectEnglish(),
				notificationSrvConfig.getNotifyCRCWhenPatientIsAddedSubjectSpanish(), updatedMessageEnglish,
				updatedMessageSpanish, userId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyPatientWhenProviderIsReplaced(Long userId) {
		addNotificationToAccount(notificationSrvConfig.getNotifyPatientWhenProvidersAreReplacedFrom(),
				notificationSrvConfig.getNotifyPatientWhenProvidersAreReplacedSubjectEnglish(),
				notificationSrvConfig.getNotifyPatientWhenProvidersAreReplacedSubjectSpanish(),
				notificationSrvConfig.getNotifyPatientWhenProvidersAreReplacedMessageEnglish(),
				notificationSrvConfig.getNotifyPatientWhenProvidersAreReplacedMessageSpanish(), userId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyProviderWhenPatientIsAdded(String patientFullName, Long userId, String patientId) {
		String replaceStringWith[] = { patientFullName, patientId };
		String replaceThisString[] = { PATIENT_FULL_NAME_PLACEHOLDER, PATIENT_ID_PLACEHOLDER };
		String updatedMessageEnglish = StringUtils.replaceEach(
				notificationSrvConfig.getNotifyProviderWhenPatientIsAddedMessageEnglish(), replaceThisString,
				replaceStringWith);
		String updatedMessageSpanish = StringUtils.replaceEach(
				notificationSrvConfig.getNotifyProviderWhenPatientIsAddedMessageSpanish(), replaceThisString,
				replaceStringWith);
		addNotificationToAccount(notificationSrvConfig.getNotifyProviderWhenPatientIsAddedFrom(),
				notificationSrvConfig.getNotifyProviderWhenPatientIsAddedSubjectEnglish(),
				notificationSrvConfig.getNotifyProviderWhenPatientIsAddedSubjectSpanish(), updatedMessageEnglish,
				updatedMessageSpanish, userId);
	}

	/**
	 * 
	 * @param from    - The sender of the message. It is usually system notification
	 * @param title   - Subject of the message
	 * @param message - Content of the actual message
	 * @param userId  - Id for the recipient of the message
	 * @return
	 */

	/**
	 * Private method that inserts a row into PortalNotification Table.
	 * 
	 * @param from           - The sender of the message. It is usually system
	 *                       notification
	 * @param subjectEnglish - Subject of the message in English
	 * @param subjectSpanish - Subject of the message in Spanish
	 * @param messageEnglish - Content of the actual message in English
	 * @param messageSpanish - Content of the actual message in Spanish
	 * @param userId         - Id for the recipient of the message
	 * @return The saved notification
	 */
	private Optional<PortalNotification> addNotificationToAccount(String from, String subjectEnglish,
			String subjectSpanish, String messageEnglish, String messageSpanish, Long userId,
			UUID groupNotificationId) {
		PortalNotification notificationObj = new PortalNotification();
		notificationObj.setMessageFrom(from);
		notificationObj.setSubjectEnglish(subjectEnglish);
		notificationObj.setSubjectSpanish(subjectSpanish);
		notificationObj.setMessageEnglish(messageEnglish);
		notificationObj.setMessageSpanish(messageSpanish);
		notificationObj.setUserId(userId);
		notificationObj.setDateGenerated(LocalDateTime.now());
		notificationObj.setViewedByUser(0);
		if (groupNotificationId != null) {
			// TODO replace with ref to request object
			// notificationObj.setGroupNotificationId(groupNotificationId.toString());
		}
		notificationObj = notificationRepo.save(notificationObj);
		return Optional.of(notificationObj);
	}

	private Optional<PortalNotification> addNotificationToAccount(String from, String subjectEnglish,
			String subjectSpanish, String messageEnglish, String messageSpanish, Long userId) {
		return addNotificationToAccount(from, subjectEnglish, subjectSpanish, messageEnglish, messageSpanish, userId,
				null);
	}

	@Override
	public void notifyPatientReminderToReadBiomarkerReport(Long userId) {
		this.addNotificationToAccount(notificationSrvConfig.getRemindPatientUnreadReportFrom(),
				notificationSrvConfig.getRemindPatientUnreadReportSubjectEnglish(),
				notificationSrvConfig.getRemindPatientUnreadReportSubjectSpanish(),
				notificationSrvConfig.getRemindPatientUnreadReportMessageEnglish(),
				notificationSrvConfig.getRemindPatientUnreadReportMessageSpanish(), userId);

	}

	@Override
	public void notifyProviderCRCReminderToReadBiomarkerReport(String patientFullName, Long userId, String patientId) {
		this.addNotification(notificationSrvConfig.getRemindCRCProviderUnreadReportFrom(),
				notificationSrvConfig.getRemindCRCProviderUnreadReportSubjectEnglish(),
				notificationSrvConfig.getRemindCRCProviderUnreadReportSubjectSpanish(),
				notificationSrvConfig.getRemindCRCProviderUnreadReportMessageEnglish(),
				notificationSrvConfig.getRemindCRCProviderUnreadReportMessageSpanish(), userId, null, patientFullName,
				patientId);

	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	public void sendGroupNotifications(PortalNotification notification, List<User> recipientGroups, String from) {
		UUID groupNotificationId = UUID.randomUUID();
		logger.info("Send Group Notification to {} users from {}", recipientGroups.size(), from);
		recipientGroups.stream().forEach(user -> {
			addNotificationToAccount(from, notification.getSubjectEnglish(), notification.getSubjectSpanish(),
					notification.getMessageEnglish(), notification.getMessageSpanish(), user.getUserId(),
					groupNotificationId);
			sendEmail(notification, user);
		});

	}

	/**
	 * Sends out email to the recipient
	 * 
	 * @param notification - details of the email content
	 * @param recipient    - User to send email to
	 */
	private void sendEmail(PortalNotification notification, User recipient) {
		if (recipient.isAllowEmailNotification()) {
			String message;
			String subject;
			if (LanguageOption.ENGLISH.equals(recipient.getPreferredLanguage())) {
				message = notification.getMessageEnglish();
				subject = notification.getSubjectEnglish();
			} else {
				message = notification.getMessageSpanish();
				subject = notification.getSubjectSpanish();
			}
			emailService.sendEmailNotification(recipient.getEmail(), null, subject, message);
		}
	}
}
