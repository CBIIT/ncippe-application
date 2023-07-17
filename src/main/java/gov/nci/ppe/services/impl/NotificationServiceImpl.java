package gov.nci.ppe.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nci.ppe.configurations.NotificationServiceConfig;
import gov.nci.ppe.constants.CommonConstants.AuditEventType;
import gov.nci.ppe.constants.CommonConstants.LanguageOption;
import gov.nci.ppe.data.entity.GroupNotificationRequest;
import gov.nci.ppe.data.entity.PortalNotification;
import gov.nci.ppe.data.entity.Role;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.data.repository.GroupNotificationRequestRepository;
import gov.nci.ppe.data.repository.PortalNotificationRepository;
import gov.nci.ppe.data.repository.RoleRepository;
import gov.nci.ppe.data.repository.UserRepository;
import gov.nci.ppe.services.AuditService;
import gov.nci.ppe.services.EmailLogService;
import gov.nci.ppe.services.NotificationService;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation class for {@link NotificationService}
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-20
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

	private static final String PATIENT_FULL_NAME_PLACEHOLDER = "%{PatientFullName}";

	private static final String PATIENT_ID_PLACEHOLDER = "%{PatientId}";

	private PortalNotificationRepository notificationRepo;

	private NotificationServiceConfig notificationSrvConfig;

	private EmailLogService emailService;

	private UserRepository userRepository;

	private AuditService auditService;

	private GroupNotificationRequestRepository groupNotificationRequestRepository;

	private RoleRepository roleRepository;

	private ObjectMapper mapper;

	@Autowired
	public NotificationServiceImpl(PortalNotificationRepository notificationRepo,
			GroupNotificationRequestRepository groupNotificationRequestRepository, RoleRepository roleRepository,
			NotificationServiceConfig notificationSrvConfig, EmailLogService emailService, AuditService auditService,
			UserRepository userRepository) {
		System.out.println("MHL NotificationServiceImpl");
		this.notificationRepo = notificationRepo;
		this.groupNotificationRequestRepository = groupNotificationRequestRepository;
		this.roleRepository = roleRepository;
		this.notificationSrvConfig = notificationSrvConfig;
		this.emailService = emailService;
		this.auditService = auditService;
		this.userRepository = userRepository;
		this.mapper = new ObjectMapper();
		System.out.println("MHL IN NotificationServiceImpl");
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
		String[] replaceStringWith = { userName, patientName, patientId };
		String[] replaceThisString = { "%{FirstName}", "%{PatientName}", PATIENT_ID_PLACEHOLDER };
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

		notificationsForUpdate.forEach(notification -> notification.setViewedByUser(1));
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
		String[] replaceStringWith = { patientFullName, patientId };
		String[] replaceThisString = { PATIENT_FULL_NAME_PLACEHOLDER, PATIENT_ID_PLACEHOLDER };
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
		String[] replaceStringWith = { patientFullName, patientId };
		String[] replaceThisString = { PATIENT_FULL_NAME_PLACEHOLDER, PATIENT_ID_PLACEHOLDER };
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
			GroupNotificationRequest groupNotification) {
		PortalNotification notificationObj = new PortalNotification();
		notificationObj.setMessageFrom(from);
		notificationObj.setSubjectEnglish(subjectEnglish);
		notificationObj.setSubjectSpanish(subjectSpanish);
		notificationObj.setMessageEnglish(messageEnglish);
		notificationObj.setMessageSpanish(messageSpanish);
		notificationObj.setUserId(userId);
		notificationObj.setDateGenerated(LocalDateTime.now());
		notificationObj.setViewedByUser(0);
		if (groupNotification != null) {
			notificationObj.setGroupNotificationRequest(groupNotification);
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
	public void sendGroupNotifications(GroupNotificationRequest groupNotification) throws JsonProcessingException {
		Set<Role> targetRoles = groupNotification.getRecipientRoles().stream()
				.map(role -> roleRepository.findByRoleName(role.getRoleName())).collect(Collectors.toSet());
		groupNotification.setRecipientRoles(targetRoles);
		groupNotification.setTimeOfRequest(LocalDateTime.now());
		GroupNotificationRequest savedRequest = groupNotificationRequestRepository.save(groupNotification);
		String from = groupNotification.getRequester().getUserUUID();
		List<User> recipientGroups = userRepository.findByRoleIn(groupNotification.getRecipientRoles());
		log.info("Send Group Notification to {} users from {}", recipientGroups.size(), from);
		recipientGroups.stream()
				.filter(user -> user.isAllowEmailNotification() && StringUtils.isNotBlank(user.getEmail()))
				.forEach(user -> {
					addNotificationToAccount(from, groupNotification.getSubjectEnglish(),
							groupNotification.getSubjectSpanish(), groupNotification.getMessageEnglish(),
							groupNotification.getMessageSpanish(), user.getUserId(), savedRequest);
					sendEmail(groupNotification, user);
				});

		ObjectNode auditDetailsNode = mapper.createObjectNode();
		auditDetailsNode.put("requester", savedRequest.getRequester().getUserUUID());
		auditDetailsNode.put("notification", mapper.writeValueAsString(savedRequest));
		auditService.logAuditEvent(auditDetailsNode, AuditEventType.PPE_SEND_GROUP_NOTIFICATION);

	}

	/**
	 * Sends out email to the recipient
	 * 
	 * @param notification - details of the email content
	 * @param recipient    - User to send email to
	 */
	private void sendEmail(GroupNotificationRequest notification, User recipient) {
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

	/*
	 * {@inheritDoc}
	 */
	@Override
	public List<GroupNotificationRequest> getGroupNotificationRequests(User requester) {

		return groupNotificationRequestRepository.findByRequester(requester);
	}
}
