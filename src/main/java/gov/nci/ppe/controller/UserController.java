package gov.nci.ppe.controller;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dozermapper.core.Mapper;

import gov.nci.ppe.constants.CommonConstants;
import gov.nci.ppe.constants.CommonConstants.AuditEventType;
import gov.nci.ppe.constants.CommonConstants.LanguageOption;
import gov.nci.ppe.constants.DatabaseConstants.PortalAccountStatus;
import gov.nci.ppe.constants.DatabaseConstants.QuestionAnswerType;
import gov.nci.ppe.constants.HttpResponseConstants;
import gov.nci.ppe.constants.PPERole;
import gov.nci.ppe.constants.UrlConstants;
import gov.nci.ppe.data.entity.CRC;
import gov.nci.ppe.data.entity.Code;
import gov.nci.ppe.data.entity.ContentEditor;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.Provider;
import gov.nci.ppe.data.entity.QuestionAnswer;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.data.entity.dto.ContentEditorDTO;
import gov.nci.ppe.data.entity.dto.CrcDTO;
import gov.nci.ppe.data.entity.dto.JsonViews;
import gov.nci.ppe.data.entity.dto.ParticipantDTO;
import gov.nci.ppe.data.entity.dto.ProviderDTO;
import gov.nci.ppe.data.entity.dto.QuestionAnswerDTO;
import gov.nci.ppe.data.entity.dto.UserDTO;
import gov.nci.ppe.exception.BusinessConstraintViolationException;
import gov.nci.ppe.exception.UuidConflictException;
import gov.nci.ppe.services.AuditService;
import gov.nci.ppe.services.AuthorizationService;
import gov.nci.ppe.services.CodeService;
import gov.nci.ppe.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller class for User related actions.
 * 
 * @author PublicisSapient
 * @version 1.0;
 * @since 2019-07-22
 */

@RestController
@Slf4j
public class UserController {

	@Autowired
	@Qualifier("dozerBean")
	private Mapper dozerBeanMapper;

	@Autowired
	private UserService userService;

	@Autowired
	private CodeService codeService;

	@Autowired
	private AuditService auditService;

	@Autowired
	AuthorizationService authService;

	@Autowired
	private MessageSource messageSource;

	private ObjectMapper mapper = new ObjectMapper();
	
	private Logger logger = Logger.getLogger(UserController.class.getName());

	@Operation(summary = "Returns the data about the logged in user. If this is the users first time logging in, it will update the database with the users UUID and activate the account")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "User data found"),
			@ApiResponse(responseCode = "409", description = "UUID of User Already in Use"),
			@ApiResponse(responseCode = "404", description = "User Not Found") })
	@PostMapping(value = "/api/v1/login", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> login(@RequestParam String uuid,
										@RequestParam String email,
										@RequestBody String idToken, Locale locale) throws JsonProcessingException {

		logger.info("Received login request");
		raiseLoginAuditEvent(uuid, email, "Attempt to Login", AuditEventType.PPE_LOGIN_ATTEMPT);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		Optional<User> userOptional;
		try {
			userOptional = userService.loginUser(uuid, email);
		} catch (UuidConflictException e) {
			logger.severe("Login conflict: UUID already in use");
			raiseLoginAuditEvent(uuid, email, "User already activated with different UUID",
					AuditEventType.PPE_LOGIN_EMAIL_UUID_CONFLICT);
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(messageSource.getMessage(HttpResponseConstants.USER_UUID_ALREADY_USED_MSG, null, locale));
		}

		if (userOptional.isEmpty()) {
			logger.info("Login failed: user not found");
			raiseLoginAuditEvent(uuid, email, "User Not Found", AuditEventType.PPE_LOGIN_USER_NOT_FOUND);
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(messageSource.getMessage(HttpResponseConstants.NO_USER_FOUND_MSG, null, locale));
		}

		raiseLoginAuditEvent(uuid, email, "Login Successful", AuditEventType.PPE_LOGIN_SUCCESS);
		String userInJsonFormat = marshalUserToJson(userOptional.get());
		return new ResponseEntity<>(userInJsonFormat, httpHeaders, HttpStatus.OK);

	}

	private void raiseLoginAuditEvent(String uuid, String email, String notes, AuditEventType eventType)
			throws JsonProcessingException {
		ObjectNode auditDetail = mapper.createObjectNode();
		auditDetail.put("UUID", uuid);
		auditDetail.put("Email", email);

		auditDetail.put("Notes", notes);
		auditService.logAuditEvent(auditDetail, eventType);
	}

	@Operation(summary = "Returns the User Details for the User with matching uuid, email, or patient id")
	@GetMapping(value = "/api/v1/user", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getUser(HttpServletRequest request,
			@Parameter(description = "Unique Id for the User", required = false) @RequestParam(value = "uuid", required = false) String userUUID,
			@Parameter(description = "email of the User", required = false) @RequestParam(value = "email", required = false) String email,
			@Parameter(description = "Patient ID", required = false) @RequestParam(value = "patientId", required = false) String patientId, @RequestParam(value = "requestingUserUUID", required = false) String requestingUserUUID,
			Locale locale) throws JsonProcessingException {

        System.out.println("MHL userUUID: " + userUUID);
        System.out.println("MHL email: " + email);
        System.out.println("MHL patientId: " + patientId);
        logger.info("MHL userUUID: " + userUUID);
        logger.info("MHL email: " +  email);
        logger.info("MHL patientId: " + patientId);

		userUUID = StringUtils.stripToEmpty(userUUID);

		email = StringUtils.stripToEmpty(email);
		patientId = StringUtils.stripToEmpty(patientId);
		return fetchUser(requestingUserUUID, userUUID, email, patientId, locale);
	}

	/**
	 * This method will update the registered user's email notification preference
	 * and phone number.
	 * 
	 * @param userGUID               - GUID of the user
	 * @param phoneNumber
	 * @param allowEmailNotification
	 * @param locale
	 * @return
	 * @throws JsonProcessingException
	 */
	@Operation(summary = "update the registered user's email notification preference and phone number.")
	@PostMapping(value = "/api/v1/user/{userGUID}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updateUser(HttpServletRequest request,
			@Parameter(description = "Unique Id for the User", required = true) @PathVariable String userGUID,
			@RequestParam(value = "requestingUserUUID", required = false) String requestingUserUUID,
			@Parameter(description = "New phone number for the User", required = true) @RequestParam(value = "phoneNumber", required = true) String phoneNumber,
			@Parameter(description = "Allow Email Notification or not", required = true) @RequestParam(value = "allowEmailNotification", required = true) Boolean allowEmailNotification,
			@Parameter(description = "Language preferred by the participant", required = false) @RequestParam(value = "preferredLanguage", required = false) String preferredLanguage,
			Locale locale) throws JsonProcessingException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		//String requestingUserUUID = request.getHeader(CommonConstants.HEADER_UUID);
        System.out.println("MHL requestingUserUUID: " + requestingUserUUID);

        if (!authService.authorize(requestingUserUUID, userGUID)) {
			return new ResponseEntity<>(
					messageSource.getMessage(HttpResponseConstants.UNAUTHORIZED_ACCESS, null, locale), httpHeaders,
					HttpStatus.UNAUTHORIZED);
		}

		userGUID = StringUtils.stripToEmpty(userGUID);
		phoneNumber = StringUtils.stripToEmpty(phoneNumber);

		LanguageOption preferredLang = null;

		try {
			preferredLang = LanguageOption.getLanguageOption(preferredLanguage);
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(messageSource.getMessage(
					HttpResponseConstants.LANGUAGE_NOT_SUPPORTED, new Object[] { preferredLanguage }, locale));
		}

		Optional<User> userOptional = userService.updateUserDetails(userGUID, allowEmailNotification, phoneNumber,
				preferredLang, requestingUserUUID);
		if (!userOptional.isPresent()) {
			return new ResponseEntity<>(messageSource.getMessage(HttpResponseConstants.NO_USER_FOUND_MSG, null, locale),
					httpHeaders, HttpStatus.NO_CONTENT);
		}

		User user = userOptional.get();
		String jsonFormat = convertUserToJSON(user);

		return new ResponseEntity<>(jsonFormat, httpHeaders, HttpStatus.OK);
	}

	/**
	 * Method to deactivate/close online account for the user.
	 * 
	 * @param request  - HTTPRequest object
	 * @param userUUID - uuid for the user who is deactivating the account.
	 * @param locale
	 * @return
	 * @throws JsonProcessingException
	 */
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "User has been deactivated"),
			@ApiResponse(responseCode = "401", description = "Bearer token is missing or expired.") })
	@Operation(summary = "Deactivates a particular user in the portal")
	@PostMapping(value = "/api/v1/deactivate-user/{userUUID}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> deActivateUserByGuid(HttpServletRequest request,
			@Parameter(description = "Unique Id for the User", required = true) @PathVariable String userUUID, @RequestParam(value = "requestingUserUUID", required = false) String requestingUserUUID, Locale locale)
			throws JsonProcessingException {
		HttpHeaders httpHeaders = createHeader();
		//String requestingUserUUID = request.getHeader(CommonConstants.HEADER_UUID);
		// assuming only close own account (with uuid)
		userUUID = StringUtils.stripToEmpty(userUUID);

		if (!authService.authorize(userUUID, userUUID)) {
			return new ResponseEntity<>(
					messageSource.getMessage(HttpResponseConstants.UNAUTHORIZED_ACCESS, null, locale), httpHeaders,
					HttpStatus.UNAUTHORIZED);
		}

		Optional<User> userOptional = userService.deactivateUserPortalAccountStatus(userUUID);
		String jsonFormat = convertUserToJSON(userOptional.get());
		return new ResponseEntity<>(jsonFormat, httpHeaders, HttpStatus.OK);
	}

	/**
	 * This method will allow a participant to withdraw from the program. A CRC can
	 * also withdraw a participant from the program.
	 * 
	 * @param request           - HTTPRequest object
	 * @param patientId         - Unique Patient Id assigned to each Patient
	 * @param updatedByUserUUID - UUID of the user responsible for withdrawing the
	 *                          participant
	 * @param qsAnsDTO          - List of Questions and Answers
	 * @param locale
	 * @return - HTTP Response with appropriate message.
	 * @throws JsonProcessingException
	 */
	@Operation(summary = "Participant withdraws from the Biobank program")
	@PostMapping(value = "/api/v1/withdraw-user-participation", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> withdrawParticipationByParticipant(HttpServletRequest request,
			@Parameter(description = "Unique Patient Id assigned to each Patient", required = true) @RequestParam String patientId,@RequestParam(value = "updatedByUser", required = false) String updatedByUserUUID,
			@Parameter(description = "List of Questions and their answers for withdrawing from PPE", required = true) @RequestBody List<QuestionAnswerDTO> qsAnsDTO,
			Locale locale) throws JsonProcessingException {

		patientId = StringUtils.stripToEmpty(patientId);
		//String updatedByUserUUID = request.getHeader(CommonConstants.HEADER_UUID);

		Optional<User> participantOptional = userService.findActiveParticipantByPatientId(patientId);
		if (!participantOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(messageSource.getMessage(HttpResponseConstants.NO_USER_FOUND_MSG, null, locale));
		}
		Participant patient = (Participant) participantOptional.get();

		HttpHeaders httpHeaders = createHeader();

		if (!authService.authorize(updatedByUserUUID, patient)) {
			return new ResponseEntity<>(
					messageSource.getMessage(HttpResponseConstants.UNAUTHORIZED_ACCESS, null, locale), httpHeaders,
					HttpStatus.UNAUTHORIZED);
		}
		logger.info("Request to withdraw Participant " + patient.getPatientId() + " by User " + updatedByUserUUID);
		Code code = codeService.getCode(QuestionAnswerType.PPE_WITHDRAW_SURVEY_QUESTION.getQuestionAnswerType());
		List<QuestionAnswer> qsAnsList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(qsAnsDTO)) {
			qsAnsDTO.forEach(qs -> {
				QuestionAnswer questionAnswer = dozerBeanMapper.map(qs, QuestionAnswer.class);
				questionAnswer.setDateAnswered(LocalDateTime.now());
				questionAnswer.setParticipantForQA(patient);
				questionAnswer.setQuestionCategory(code);
				qsAnsList.add(questionAnswer);
			});
		}
		/*
		 * Find out if the participant withdrew themselves or the CRC did it for them
		 * and set the LastRevisedUser accordingly
		 */
		if (StringUtils.equals(patient.getUserUUID(), updatedByUserUUID)) {
			patient.setLastRevisedUser(patient.getUserId());
		} else {
			Optional<User> crcOptional = userService.findByUuid(updatedByUserUUID);
			if (!crcOptional.isEmpty()) {
				patient.setLastRevisedUser(crcOptional.get().getUserId());
			}
		}
		Optional<User> userOptional = userService.withdrawParticipationFromBiobankProgramAndSendNotification(patient,
				qsAnsList);
		Participant withdrawnPatient = (Participant) userOptional.get();
		logger.info("Patient " + withdrawnPatient.getPatientId() + " new status "
				+ withdrawnPatient.getPortalAccountStatus().getCodeName());
		raiseWithdrawParticipationAuditEvent(patientId, updatedByUserUUID);
		String jsonFormat = convertUserToJSON(withdrawnPatient);
		return new ResponseEntity<>(jsonFormat, httpHeaders, HttpStatus.OK);
	}

	@Operation(summary = "CRC will invite a new Patient added from OPEN to participate in the portal by filling in the patient's name and email")
	@PostMapping(value = "/api/v1/user/invite-participant-to-portal", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> inviteParticipant(HttpServletRequest request,
			@Parameter(description = "Patient Id of the participant", required = true)
			@RequestParam(value = "patientId", required = true) String patientId, @RequestParam(value = "updatedByUser", required = false) String updatedByUserUUID,
			Locale locale) throws JsonProcessingException {

		patientId = StringUtils.stripToEmpty(patientId);
		//String updatedByUserUUID = request.getHeader(CommonConstants.HEADER_UUID);

		List<String> validAccountStatusList = new ArrayList<>();
		validAccountStatusList.add(PortalAccountStatus.ACCT_NEW.name());
		Optional<User> participantOptional = userService.findByPatientIdAndPortalAccountStatus(patientId,
				validAccountStatusList);
		log.info("Particpant invite-participant-to-portal ");
		if (participantOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(messageSource.getMessage(HttpResponseConstants.NO_USER_FOUND_MSG, null, locale));
		}

		HttpHeaders httpHeaders = createHeader();

		if (!authService.authorize(updatedByUserUUID, participantOptional.get())) {
			return new ResponseEntity<>(
					messageSource.getMessage(HttpResponseConstants.UNAUTHORIZED_ACCESS, null, locale), httpHeaders,
					HttpStatus.UNAUTHORIZED);
		}

		participantOptional = userService.invitePatientToPortal(patientId, updatedByUserUUID);

		String jsonFormat = convertUserToJSON(participantOptional.get());
		return new ResponseEntity<>(jsonFormat, httpHeaders, HttpStatus.OK);
	}

	@Operation(summary = "CRC will invite a new Patient added from OPEN to participate in the portal by filling in the patient's name and email")
	@PostMapping(value = "/api/v1/user/enter-new-participant-details", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "User invited to PPE Portal"),
			@ApiResponse(responseCode = "401", description = "Not Authorized"),
			@ApiResponse(responseCode = "409", description = "User has already been activated with a different UUID."),
			@ApiResponse(responseCode = "404", description = "User not found"),
			@ApiResponse(responseCode = "406", description = "Language Not Supported") })
	public ResponseEntity<String> enterUserDetails(HttpServletRequest request, @RequestParam(value = "updatedByUser", required = false) String updatedByUserUUID,
			@Parameter(description = "Patient Id of the participant", required = true) @RequestParam(value = "patientId", required = true) String patientId,
			@Parameter(description = "First name of the participant", required = true) @RequestParam(value = "firstName", required = true) String firstName,
			@Parameter(description = "Last name of the participant", required = true) @RequestParam(value = "lastName", required = true) String lastName,
			@Parameter(description = "Email Id for the participant", required = true) @RequestParam(value = "emailId", required = true) String emailId,
			@Parameter(description = "Language preferred by the participant", required = true) @RequestParam(value = "preferredLanguage", required = true) String preferredLanguage,
			Locale locale) throws JsonProcessingException {
		//String updatedByUserUUID = request.getHeader(CommonConstants.HEADER_UUID);

		patientId = StringUtils.stripToEmpty(patientId);
		firstName = StringUtils.stripToEmpty(firstName);
		lastName = StringUtils.stripToEmpty(lastName);
//		if(StringUtils.stripToEmpty(emailId).isBlank()) {
//			System.out.println(" empty email " + StringUtils.stripToEmpty(emailId));
//		}else {
//			System.out.println(" non-empty email " + StringUtils.stripToEmpty(emailId));
//		}
		emailId = (StringUtils.stripToEmpty(emailId)).isBlank()?null:StringUtils.stripToEmpty(emailId);

		LanguageOption preferredLang = null;

		try {
			preferredLang = LanguageOption.getLanguageOption(preferredLanguage);
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(messageSource.getMessage(
					HttpResponseConstants.LANGUAGE_NOT_SUPPORTED, new Object[] { preferredLanguage }, locale));
		}

		List<String> validAccountStatusList = new ArrayList<>();
		validAccountStatusList.add(PortalAccountStatus.ACCT_NEW.name());
		Optional<User> participantOptional = userService.findByPatientIdAndPortalAccountStatus(patientId,
				validAccountStatusList);
		if (participantOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(messageSource.getMessage(HttpResponseConstants.NO_USER_FOUND_MSG, null, locale));
		}

		HttpHeaders httpHeaders = createHeader();

		User newPatient = participantOptional.get();
		if (!authService.authorize(updatedByUserUUID, newPatient)) {
			return new ResponseEntity<>(
					messageSource.getMessage(HttpResponseConstants.UNAUTHORIZED_ACCESS, null, locale), httpHeaders,
					HttpStatus.UNAUTHORIZED);
		}

		// Fill in the details
		newPatient.setFirstName(firstName);
		newPatient.setLastName(lastName);
		newPatient.setEmail(emailId);
		newPatient.setPreferredLanguage(preferredLang);

		List<String> accountStatusList = new ArrayList<>();
		accountStatusList.add(PortalAccountStatus.ACCT_ACTIVE.name());
		Optional<User> optionalCRC = userService.findByUuidAndPortalAccountStatus(updatedByUserUUID, accountStatusList);
		// No need to check for existence, as otherwise authorize() call would have
		// failed
		newPatient.setLastRevisedUser(optionalCRC.get().getUserId());
		participantOptional = userService.updateUser(newPatient);

		String jsonFormat = convertUserToJSON(participantOptional.get());
		return new ResponseEntity<>(jsonFormat, httpHeaders, HttpStatus.OK);
	}

	private HttpHeaders createHeader() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
		httpHeaders.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		return httpHeaders;
	}

	/**
	 * Converts a user loaded outside a service transaction (e.g. after a read-only
	 * find) into JSON. Re-loads and initializes lazy associations when open-in-view
	 * is disabled.
	 */
	private String convertUserToJSON(User user) throws JsonProcessingException {
		User ready = userService.prepareUserForDetailSerialization(Optional.of(user))
				.orElseThrow(() -> new IllegalStateException("User not found during serialization prep"));
		return marshalUserToJson(ready);
	}

	/**
	 * Marshals an already-initialized user (e.g. from {@link UserService#loginUser})
	 * to JSON without an extra round trip.
	 */
	private String marshalUserToJson(User user) throws JsonProcessingException {
		UserDTO userDTO = convertUserDTO(user);

		mapper.registerSubtypes(new NamedType(ParticipantDTO.class, "ParticipantDTO"),
				new NamedType(ProviderDTO.class, "ProviderDTO"), new NamedType(CrcDTO.class, "CrcDTO"));
		PPERole roleName = PPERole.valueOf(user.getRole().getRoleName());
		switch (roleName) {
		case ROLE_PPE_PROVIDER:
			return mapper.writerWithView(JsonViews.ProviderDetailView.class).writeValueAsString(userDTO);

		case ROLE_PPE_PARTICIPANT:
			return mapper.writerWithView(JsonViews.ParticipantDetailView.class).writeValueAsString(userDTO);

		case ROLE_PPE_CRC:
			return mapper.writerWithView(JsonViews.CrcDetailView.class).writeValueAsString(userDTO);

		default:
			return mapper.writerWithView(JsonViews.UsersSummaryView.class).writeValueAsString(userDTO);
		}
	}

	private UserDTO convertUserDTO(User usr) {
		UserDTO userDTO = null;
		PPERole roleName = PPERole.valueOf(usr.getRole().getRoleName());

		switch (roleName) {
		case ROLE_PPE_PROVIDER:
			Provider provider = (Provider) usr;
			ProviderDTO providerDTO = dozerBeanMapper.map(provider, ProviderDTO.class);

			// Special case for providers, filter out associated patients who have not been
			// initiated.
			providerDTO.getPatients().removeIf(
					patient -> patient.getPortalAccountStatus().equalsIgnoreCase(PortalAccountStatus.ACCT_NEW.name()));

			userDTO = providerDTO;
			break;

		case ROLE_PPE_PARTICIPANT:
			Participant patient = (Participant) usr;

			ParticipantDTO participantDTO = dozerBeanMapper.map(patient, ParticipantDTO.class);
			// Filter out Notifications for CRC and Providers
			// if (participantDTO.getCrc().getNotifications() != null) {
			// 	participantDTO.getCrc().getNotifications().clear();
			// }
			participantDTO.getCrcsSet().forEach(crc -> {
				if (crc.getNotifications() != null) {
				 	crc.getNotifications().clear();
				}
			});
			participantDTO.getProviders().forEach(associatedProvider -> {
				if (associatedProvider.getNotifications() != null) {
					associatedProvider.getNotifications().clear();
				}
			});
			userDTO = participantDTO;
			break;

		case ROLE_PPE_CRC:
			CRC crcAdmin = (CRC) usr;
			userDTO = dozerBeanMapper.map(crcAdmin, CrcDTO.class);
			break;

		case ROLE_PPE_CONTENT_EDITOR:
			ContentEditor contentEditor = (ContentEditor) usr;
			userDTO = dozerBeanMapper.map(contentEditor, ContentEditorDTO.class);
			break;

		default:
			userDTO = dozerBeanMapper.map(usr, UserDTO.class);
		}
		return userDTO;

	}

	private void raiseWithdrawParticipationAuditEvent(String patientId, String uuid) throws JsonProcessingException {
		ObjectNode auditDetail = mapper.createObjectNode();

		auditDetail.put("UUID", uuid).put("PatientID", patientId);
		auditService.logAuditEvent(auditDetail, AuditEventType.PPE_WITHDRAW_FROM_PROGRAM);
	}

	private ResponseEntity<String> fetchUser( String requestingUserUUID, String uuid, String email, String patientId,
			Locale locale) throws JsonProcessingException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		Optional<User> userOptional = Optional.empty();
		//System.out.println(" line 565 uuid " + uuid);

		if (StringUtils.isNotBlank(uuid)) {
			userOptional = userService.findByUuidAndPortalAccountStatus(uuid, PortalAccountStatus.names());
		} else if (StringUtils.isNotBlank(email)) {
			userOptional = userService.findByEmailAndPortalAccountStatus(email, PortalAccountStatus.names());

		} else if (StringUtils.isNotBlank(patientId)) {
			userOptional = userService.findByPatientIdAndPortalAccountStatus(patientId, PortalAccountStatus.names());
		} else {
			return ResponseEntity.badRequest().build();
		}
		if (!userOptional.isPresent()) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(httpHeaders)
					.body(messageSource.getMessage(HttpResponseConstants.NO_USER_FOUND_MSG, null, locale));
		}
		User user = userOptional.get();
		System.out.println(" line 582 user id" + user.getUserId());

		logger.info("MHL fetchUser requestingUserUUID: " + requestingUserUUID);
		logger.info("MHL fetchUser user:\n " + user.getUserUUID() + "\n");
		if (!authService.authorize(requestingUserUUID, user)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(httpHeaders)
					.body(messageSource.getMessage(HttpResponseConstants.UNAUTHORIZED_ACCESS, null, locale));
		}

		String userJson = convertUserToJSON(user);
		return new ResponseEntity<>(userJson, httpHeaders, HttpStatus.OK);
	}

	@Operation(summary = "CRC updates an existing participant's email by supplying the Patient Id and the new email")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Participant Email updated"),
			@ApiResponse(responseCode = "403", description = "CRC Not Authorized to alter the participant's email."),
			@ApiResponse(responseCode = "409", description = "User has already been activated. Cannot change email."),
			@ApiResponse(responseCode = "404", description = "User not found"),
			@ApiResponse(responseCode = "406", description = "Language Not Supported") })
	@PostMapping(value = UrlConstants.URL_USER_UPDATE_EMAIL, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updateParticipantEmail(HttpServletRequest request,
			@Parameter(description = "Patient Id of the Participant", required = true) @RequestParam(value = UrlConstants.REQ_PARAM_PATIENT_ID, required = true) String patientId,
			@Parameter(description = "New email for the Participant", required = true) @RequestParam(value = UrlConstants.REQ_PARAM_EMAIL, required = true) String email, @RequestParam(value = "requestingUserUUID", required = true) String requestingUserUUID,
			Locale locale) throws JsonProcessingException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		//String requestingUserUUID = request.getHeader(CommonConstants.HEADER_UUID);
		
		Optional<User> userOptional = userService.findByPatientIdAndPortalAccountStatus(patientId, PortalAccountStatus.names());
		if (!userOptional.isPresent()) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(httpHeaders)
					.body(messageSource.getMessage(HttpResponseConstants.NO_USER_FOUND_MSG, null, locale));
		}
		User user = userOptional.get();

		if (!authService.authorize(requestingUserUUID, user)) {
			return new ResponseEntity<>(
					messageSource.getMessage(HttpResponseConstants.UNAUTHORIZED_ACCESS, null, locale), httpHeaders,
					HttpStatus.FORBIDDEN);
		}
		try {
			Optional<User> updatedUserOpt = userService.updatePatientEmail(patientId, email, requestingUserUUID);
			String userJson = convertUserToJSON(updatedUserOpt.get());
			return new ResponseEntity<>(userJson, httpHeaders, HttpStatus.OK);
		} catch (BusinessConstraintViolationException ex) {
			return new ResponseEntity<String>(
					messageSource.getMessage(HttpResponseConstants.PARTICIPANT_EMAIL_UNALTERABLE,
							new Object[] { patientId }, locale),
					httpHeaders, HttpStatus.CONFLICT);
		}
	}

}
