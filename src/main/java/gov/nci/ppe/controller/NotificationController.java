package gov.nci.ppe.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.GsonBuilder;

import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.PortalNotification;
import gov.nci.ppe.data.entity.Provider;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.services.NotificationService;
import gov.nci.ppe.services.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller class for User related actions.
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-20
 */

@RestController
public class NotificationController {

	@Autowired
	private NotificationService notificationService;

	@Autowired
	public UserService userService;

	public NotificationController() {
	}

	/**
	 * This method supports insertion of a new notification for a particular user
	 * 
	 * @param userGUID - User GUID
	 * @param message  - Notification message
	 * @return HTTP OK if the notification message is inserted else
	 */
	@ApiOperation(value = "Creates a new notification for a particular user")
	@PostMapping(value = "/api/v1/user/{userGUID}/notification")

	public ResponseEntity<String> addNotificationForUser(
			@ApiParam(value = "Unique ID of the User to be notified", required = true) @PathVariable String userGUID,
			@ApiParam(value = "Notification Message", required = true) @RequestParam(value = "message", required = true) String message,
			@ApiParam(value = "Source of the Notification", required = true) @RequestParam(value = "from", required = true) String from,
			@ApiParam(value = "Notification Subject", required = true) @RequestParam(value = "subject", required = true) String subject) {

		Optional<User> userOptional = userService.findByUuid(userGUID);
		List<PortalNotification> notificationList = new ArrayList<>();
		if (userOptional.isPresent()) {
			Participant patient = (Participant) userOptional.get();

			Map<Long, String> userDetailMap = new HashMap<Long, String>();
			Set<Provider> providers = patient.getProviders();
			userDetailMap.put(patient.getUserId(), patient.getFirstName());
			providers.forEach(provider -> {
				userDetailMap.put(provider.getUserId(), provider.getFirstName());
			});

			userDetailMap.forEach((userId, userName) -> {
				Optional<PortalNotification> notificationOptional = notificationService.addNotification(from, subject,
						message, userId, userName, patient.getFirstName(), patient.getPatientId());
				if (notificationOptional.isPresent()) {
					notificationList.add(notificationOptional.get());
				}
			});

			String notificationInJsonFormat = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
					.toJson(notificationList);
			return ResponseEntity.status(HttpStatus.CREATED).body(notificationInJsonFormat);
		}
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body("Could not create the record. Check the userGUID");
	}

	/**
	 * Retrieves all notifications for the specified User
	 * 
	 * @param userGUID - Unique ID of the User whose notifications are to be
	 *                 retrieved
	 * @return List of notification objects
	 */
	@ApiOperation(value = "Retrieves all notifications for the specified User")
	@GetMapping(value = "/api/v1/user/{userGUID}/notifications")
	public ResponseEntity<String> getAllNotificationsForUser(
			@ApiParam(value = "Unique ID of the User whose notifications are to be retrieved", required = true) @PathVariable String userGUID) {

		Optional<User> userOptional = userService.findByUuid(userGUID);

		if (userOptional.isPresent()) {
			List<PortalNotification> notificationList = notificationService
					.getAllNotificationsForUserByUserId(userOptional.get().getUserId());
			if (CollectionUtils.isNotEmpty(notificationList)) {
				String notificationInJsonFormat = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
						.setPrettyPrinting().disableHtmlEscaping().create().toJson(notificationList);
				return ResponseEntity.status(HttpStatus.OK).body(notificationInJsonFormat);
			}
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No notifications found for "
					+ userOptional.get().getFirstName() + " " + userOptional.get().getLastName());
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User is not present");
	}

	/**
	 * Retrieve the notification for the given user with the specified notification
	 * id
	 * 
	 * @param userGUID       - Unique ID of the User whose notification is to be
	 *                       retrieved
	 * @param notificationId - Unique ID of the Notification to retrieve
	 * @return the Notifcation object
	 */
	@ApiOperation(value = "Retrieve the notification for the given user with the specified notification id")
	@GetMapping(value = "/api/v1/user/{userGUID}/notification/{notificationId}")
	public ResponseEntity<String> getNotificationForNotificationId(
			@ApiParam(value = "Unique ID of the User whose notification is to be retrieved", required = true) @PathVariable String userGUID,
			@ApiParam(value = "Unique ID of the Notification to retrieve", required = true) @PathVariable String notificationId) {

		Optional<PortalNotification> notificationOptional = notificationService
				.getNotificationByNotificationId(Long.valueOf(notificationId));
		if (notificationOptional.isPresent()) {

			String notificationInJsonFormat = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
					.setPrettyPrinting().disableHtmlEscaping().create().toJson(notificationOptional.get());
			return ResponseEntity.status(HttpStatus.OK).body(notificationInJsonFormat);
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("notificationInJsonFormat");
	}

	/**
	 * Mark all notifications as read for a given user
	 * 
	 * @param userGUID - Unique ID of the User whose notifications are to be marked
	 *                 as read
	 * @return List of User Notifications with Read indicator set
	 */
	@ApiOperation(value = "Mark all notifications as read for a given user")
	@PutMapping(value = "/api/v1/user/{userGUID}/notifications/mark-as-read")
	public ResponseEntity<String> updateAllNotificationsForUserAsReadByUserGUID(
			@ApiParam(value = "Unique ID of the User whose notifications are to be marked as read", required = true) @PathVariable String userGUID) {

		Optional<User> userOptional = userService.findByUuid(userGUID);

		if (userOptional.isPresent()) {
			List<PortalNotification> notificationList = notificationService
					.getAllNotificationsForUserByUserId(userOptional.get().getUserId());

			notificationList.forEach(notification -> {
				notification.setViewedByUser(1);
			});

			List<PortalNotification> updatedNotificationList = notificationService
					.updateAllNotificationsForUserAsReadByUserGUID(notificationList);

			if (CollectionUtils.isNotEmpty(updatedNotificationList)) {
				String notificationInJsonFormat = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
						.setPrettyPrinting().disableHtmlEscaping().create().toJson(updatedNotificationList);
				return ResponseEntity.status(HttpStatus.OK).body(notificationInJsonFormat);
			}
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No notifications found for "
					+ userOptional.get().getFirstName() + " " + userOptional.get().getLastName());
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User is not present");
	}

	/**
	 * Mark an individual notification as read for a given user
	 * 
	 * @param userGUID       - Unique ID of the User whose notifications are to be
	 *                       marked as read
	 * @param notificationId - Unique ID of the Notification to be marked as read
	 * @return
	 */
	@ApiOperation(value = "Mark an individual notification as read for a given user")
	@PutMapping(value = "/api/v1/user/{userGUID}/notification/{notificationId}/mark-as-read")
	public ResponseEntity<String> updateNotificationAsReadByNotificationId(
			@ApiParam(value = "Unique ID of the User whose notifications are to be marked as read", required = true) @PathVariable String userGUID,
			@ApiParam(value = "Unique ID of the Notification to be marked as read", required = true) @PathVariable String notificationId) {
		Optional<PortalNotification> notifyOptional = notificationService
				.getNotificationByNotificationId(Long.valueOf(notificationId));

		if (notifyOptional.isPresent()) {

			PortalNotification notificationObjectForUpdate = notifyOptional.get();
			notificationObjectForUpdate.setViewedByUser(1);
			notifyOptional = notificationService.updateNotificationAsReadByNotificationId(notificationObjectForUpdate);
			if (notifyOptional.get().getViewedByUser() == 1) {
				String notificationInJsonFormat = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
						.setPrettyPrinting().disableHtmlEscaping().create().toJson(notifyOptional.get());
				return ResponseEntity.status(HttpStatus.OK).body(notificationInJsonFormat);
			}

			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Notification not found for " + notificationId);
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User " + userGUID + " is not present");
	}

}
