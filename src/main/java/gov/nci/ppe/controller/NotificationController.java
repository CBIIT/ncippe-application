package gov.nci.ppe.controller;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nci.ppe.constants.CommonConstants;
import gov.nci.ppe.constants.HttpResponseConstants;
import gov.nci.ppe.constants.PPERole;
import gov.nci.ppe.data.entity.PortalNotification;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.data.entity.dto.NotificationSendRequestDto;
import gov.nci.ppe.data.entity.dto.PortalNotificationDTO;
import gov.nci.ppe.services.NotificationService;
import gov.nci.ppe.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import com.github.dozermapper.core.Mapper;

/**
 * Controller class for User related actions.
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-20
 */

@Api
@RestController
public class NotificationController {

	private static final Logger logger = LogManager.getLogger(NotificationController.class);

	private NotificationService notificationService;

	private MessageSource messageSource;

	private UserService userService;

	private ObjectMapper mapper;

	private Mapper dozerBeanMapper;

	@Autowired
	public NotificationController(NotificationService notificationService, MessageSource messageSource,
			UserService userService, @Qualifier("dozerBean") Mapper dozerBeanMapper) {
		this.mapper = new ObjectMapper();
		this.mapper.registerSubtypes(PortalNotificationDTO.class);
		this.notificationService = notificationService;
		this.messageSource = messageSource;
		this.userService = userService;
		this.dozerBeanMapper = dozerBeanMapper;
	}

	/**
	 * Retrieves all notifications for the specified User
	 * 
	 * @param userGUID - Unique ID of the User whose notifications are to be
	 *                 retrieved
	 * @return List of notification objects
	 */
	@ApiOperation(value = "Retrieves all notifications for the specified User", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = {
			@ApiResponse(code = org.apache.http.HttpStatus.SC_OK, message = "All available User notifications fetched"),
			@ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authorized to view messages for other users") })
	@GetMapping(value = "/api/v1/user/{userGUID}/notifications")
	public ResponseEntity<String> getAllNotificationsForUser(HttpServletRequest request,
			@ApiParam(value = "Unique ID of the User whose notifications are to be retrieved", required = true) @PathVariable String userGUID,
			Locale locale) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		String requestingUserUUID = request.getHeader(CommonConstants.HEADER_UUID);

		// An user can request notifications only for self.
		if (!userGUID.equals(requestingUserUUID)) {
			return new ResponseEntity<String>(
					messageSource.getMessage(HttpResponseConstants.UNAUTHORIZED_ACCESS, null, locale), httpHeaders,

					HttpStatus.UNAUTHORIZED);
		}

		Optional<User> userOptional = userService.findByUuid(userGUID);

		if (userOptional.isPresent()) {
			List<PortalNotification> notificationList = notificationService
					.getAllNotificationsForUserByUserId(userOptional.get().getUserId());
			if (CollectionUtils.isNotEmpty(notificationList)) {

				String notificationInJsonFormat;
				try {
					notificationInJsonFormat = mapper.writeValueAsString(notificationList);
					return ResponseEntity.status(HttpStatus.OK).body(notificationInJsonFormat);
				} catch (JsonProcessingException e) {
					logger.catching(e);
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(httpHeaders)
							.body(messageSource.getMessage(HttpResponseConstants.INTERNAL_SERVER_ERROR, null, locale));
				}

			}
			return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(httpHeaders)
					.body(messageSource.getMessage(HttpResponseConstants.NOTIFICATION_NOT_FOUND_FOR_USER,
							new Object[] { userOptional.get().getFullName() }, locale));
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(httpHeaders)
				.body(messageSource.getMessage(HttpResponseConstants.NO_USER_FOUND_MSG, null, locale));
	}

	/**
	 * Retrieve the notification for the given user with the specified notification
	 * id
	 * 
	 * @param userGUID       - Unique ID of the User whose notification is to be
	 *                       retrieved
	 * @param notificationId - Unique ID of the Notification to retrieve
	 * @return the Notification object
	 */
	@ApiOperation(value = "Retrieve the notification for the given user with the specified notification id")
	@GetMapping(value = "/api/v1/user/{userGUID}/notification/{notificationId}")
	public ResponseEntity<String> getNotificationForNotificationId(HttpServletRequest request,
			@ApiParam(value = "Unique ID of the User whose notification is to be retrieved", required = true) @PathVariable String userGUID,
			@ApiParam(value = "Unique ID of the Notification to retrieve", required = true) @PathVariable String notificationId,
			Locale locale) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		String requestingUserUUID = request.getHeader(CommonConstants.HEADER_UUID);

		// An user can request notifications only for self.
		if (!userGUID.equals(requestingUserUUID)) {
			return new ResponseEntity<String>(
					messageSource.getMessage(HttpResponseConstants.UNAUTHORIZED_ACCESS, null, locale), httpHeaders,

					HttpStatus.UNAUTHORIZED);
		}

		Optional<PortalNotification> notificationOptional = notificationService
				.getNotificationByNotificationId(Long.valueOf(notificationId));
		if (notificationOptional.isPresent()) {

			try {
				String notificationInJsonFormat = mapper.writeValueAsString(notificationOptional.get());
				return ResponseEntity.status(HttpStatus.OK).body(notificationInJsonFormat);
			} catch (JsonProcessingException e) {
				logger.catching(e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(httpHeaders)
						.body(messageSource.getMessage(HttpResponseConstants.INTERNAL_SERVER_ERROR, null, locale));
			}
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(httpHeaders).body(messageSource
				.getMessage(HttpResponseConstants.NOTIFICATION_NOT_FOUND, new Object[] { notificationId }, locale));
	}

	/**
	 * Mark all notifications as read for a given user
	 * 
	 * @param userGUID - Unique ID of the User whose notifications are to be marked
	 *                 as read
	 * @param locale
	 * @return List of User Notifications with Read indicator set
	 */
	@ApiOperation(value = "Mark all notifications as read for a given user")
	@PostMapping(value = "/api/v1/user/{userGUID}/notifications/mark-as-read")
	public ResponseEntity<String> updateAllNotificationsForUserAsReadByUserGUID(HttpServletRequest request,
			@ApiParam(value = "Unique ID of the User whose notifications are to be marked as read", required = true) @PathVariable String userGUID,
			Locale locale) {

		HttpHeaders httpHeaders = createHeader();
		String requestingUserUUID = request.getHeader(CommonConstants.HEADER_UUID);

		// An user can request notifications only for self.
		if (!userGUID.equals(requestingUserUUID)) {
			return new ResponseEntity<String>(
					messageSource.getMessage(HttpResponseConstants.UNAUTHORIZED_ACCESS, null, locale), httpHeaders,

					HttpStatus.UNAUTHORIZED);
		}

		Optional<User> userOptional = userService.findByUuid(userGUID);

		if (userOptional.isPresent()) {

			List<PortalNotification> updatedNotificationList = notificationService
					.updateAllNotificationsForUserAsReadByUserGUID(userOptional.get());

			if (CollectionUtils.isNotEmpty(updatedNotificationList)) {
				String notificationInJsonFormat;
				try {
					notificationInJsonFormat = mapper.writeValueAsString(updatedNotificationList);
					return ResponseEntity.status(HttpStatus.OK).body(notificationInJsonFormat);
				} catch (JsonProcessingException e) {
					logger.catching(e);
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(httpHeaders)
							.body(messageSource.getMessage(HttpResponseConstants.INTERNAL_SERVER_ERROR, null, locale));
				}
			}
			return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(httpHeaders)
					.body(messageSource.getMessage(HttpResponseConstants.NOTIFICATION_NOT_FOUND_FOR_USER,
							new Object[] { userOptional.get().getFullName() }, locale));
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(httpHeaders)
				.body(messageSource.getMessage(HttpResponseConstants.NO_USER_FOUND_MSG, null, locale));
	}

	private HttpHeaders createHeader() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
		httpHeaders.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		return httpHeaders;
	}

	/**
	 * Mark an individual notification as read for a given user
	 * 
	 * @param userGUID       - Unique ID of the User whose notifications are to be
	 *                       marked as read
	 * @param notificationId - Unique ID of the Notification to be marked as read
	 * @param locale
	 * @return
	 */
	@ApiOperation(value = "Mark an individual notification as read for a given user")

	@PostMapping(value = "/api/v1/user/{userGUID}/notification/{notificationId}/mark-as-read")
	public ResponseEntity<String> updateNotificationAsReadByNotificationId(HttpServletRequest request,
			@ApiParam(value = "Unique ID of the User whose notifications are to be marked as read", required = true) @PathVariable String userGUID,
			@ApiParam(value = "Unique ID of the Notification to be marked as read", required = true) @PathVariable String notificationId,
			Locale locale) {

		HttpHeaders httpHeaders = createHeader();

		String requestingUserUUID = request.getHeader(CommonConstants.HEADER_UUID);

		// An user can request notifications only for self.
		if (!userGUID.equals(requestingUserUUID)) {
			return new ResponseEntity<String>(
					messageSource.getMessage(HttpResponseConstants.UNAUTHORIZED_ACCESS, null, locale), httpHeaders,

					HttpStatus.UNAUTHORIZED);
		}

		Optional<PortalNotification> notifyOptional = notificationService
				.getNotificationByNotificationId(Long.valueOf(notificationId));

		if (notifyOptional.isPresent()) {

			PortalNotification notificationObjectForUpdate = notifyOptional.get();
			notificationObjectForUpdate.setViewedByUser(1);
			notifyOptional = notificationService.updateNotificationAsReadByNotificationId(notificationObjectForUpdate);
			if (notifyOptional.get().getViewedByUser() == 1) {
				try {
					String notificationInJsonFormat = mapper.writeValueAsString(notifyOptional.get());
					return ResponseEntity.status(HttpStatus.OK).body(notificationInJsonFormat);
				} catch (JsonProcessingException e) {
					logger.catching(e);
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(httpHeaders)
							.body(messageSource.getMessage(HttpResponseConstants.INTERNAL_SERVER_ERROR, null, locale));
				}
			}

			return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(httpHeaders).body(messageSource
					.getMessage(HttpResponseConstants.NOTIFICATION_NOT_FOUND, new Object[] { notificationId }, locale));
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(httpHeaders)
				.body(messageSource.getMessage(HttpResponseConstants.NO_USER_FOUND_MSG, null, locale));
	}

	/**
	 * Rest endpoint to send a Portal Notification to recipients
	 * 
	 * @param request - the HTTP Request
	 * @param message - The Notification to send
	 * @return
	 */
	@ApiOperation(value = "Send message to all users of specified type(s)")
	@ApiResponses(value = {
			@ApiResponse(code = org.apache.http.HttpStatus.SC_CREATED, message = "Message succesfully sent"),
			@ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND, message = "Requesting User not found"),
			@ApiResponse(code = org.apache.http.HttpStatus.SC_BAD_REQUEST, message = "Invalid Request"),
			@ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not Authorized to send messages") })
	@PostMapping(value = "/api/v1/notifications", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.TEXT_HTML_VALUE })
	public ResponseEntity<String> sendNotification(HttpServletRequest request,
			@ApiParam(value = "Details of Message to be sent", required = true, allowMultiple = false) @Valid @RequestBody NotificationSendRequestDto message,
			Locale locale) {
		HttpHeaders httpHeaders = createHeader();

		// Obtain the User record from the database to check if they are registered
		String requestingUserUUID = request.getHeader(CommonConstants.HEADER_UUID);
		Optional<User> requesterOpt = userService.findByUuid(requestingUserUUID);
		if (requesterOpt.isEmpty()) {
			return new ResponseEntity<String>(
					messageSource.getMessage(HttpResponseConstants.NO_USER_FOUND_MSG, null, locale), httpHeaders,
					HttpStatus.NOT_FOUND);
		}

		// Verify that they are authorized to send messages
		User requester = requesterOpt.get();
		if (!PPERole.ROLE_PPE_MESSAGER.name().equals(requester.getRole().getRoleName())) {
			logger.error("User {} with role {} not authorized to send messsages ", requestingUserUUID,
					requester.getRole());
			return new ResponseEntity<String>(
					messageSource.getMessage(HttpResponseConstants.UNAUTHORIZED_ACCESS, null, locale), httpHeaders,
					HttpStatus.FORBIDDEN);
		}
		PortalNotification messageToSend = dozerBeanMapper.map(message, PortalNotification.class);
		List<User> recipientGroups = userService.getUsersOfType(message.getAudiences());
		notificationService.sendGroupNotifications(messageToSend, recipientGroups, requestingUserUUID);
		return new ResponseEntity<String>(HttpStatus.CREATED);

	}

}
