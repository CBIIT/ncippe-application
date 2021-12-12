package gov.nci.ppe.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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
import gov.nci.ppe.services.AuditService;
import gov.nci.ppe.services.AuthorizationService;
import gov.nci.ppe.services.CodeService;
import gov.nci.ppe.services.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
	public UserService userService;

	@Autowired
	private CodeService codeService;

	@Autowired
	private AuditService auditService;

	@Autowired
	AuthorizationService authService;

	@Autowired
	private MessageSource messageSource;

	private ObjectMapper mapper = new ObjectMapper();

	@ApiOperation("Returns the data about the logged in user. If this is the users first time logging in, it will update the database with the users UUID and activate the account")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "User data found"),
			@ApiResponse(code = 409, message = "UUID of User Already in Use"),
			@ApiResponse(code = 404, message = "User Not Found") })
	@PostMapping(value = "/api/v1/login", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> login(HttpServletRequest request, Locale locale) throws JsonProcessingException {

		String uuid = request.getHeader(CommonConstants.HEADER_UUID);
		String email = request.getHeader(CommonConstants.HEADER_EMAIL);

		log.info("Received Login request with uuid {} and email {}", uuid, email);
		raiseLoginAuditEvent(uuid, email, "Attempt to Login", AuditEventType.PPE_LOGIN_ATTEMPT);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		List<String> accountStatusList = new ArrayList<>();
		accountStatusList.add(PortalAccountStatus.ACCT_ACTIVE.name());

		Optional<User> userOptional = userService.findByUuidAndPortalAccountStatus(uuid, accountStatusList);
		if (userOptional.isEmpty()) {
			userOptional = userService.activateUser(email, uuid);

			if (!userOptional.isPresent()) {
				log.error("Did not find user with {} and {} ", email, uuid);

				raiseLoginAuditEvent(uuid, email, "User Not Found", AuditEventType.PPE_LOGIN_USER_NOT_FOUND);

				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(messageSource.getMessage(HttpResponseConstants.NO_USER_FOUND_MSG, null, locale));
			}

			User user = userOptional.get();
			if (!user.getUserUUID().equalsIgnoreCase(uuid)) {
				log.error("Did not find user with {} and {} ", email, uuid);
				raiseLoginAuditEvent(uuid, email, "User already activated with different UUID",
						AuditEventType.PPE_LOGIN_EMAIL_UUID_CONFLICT);
				return ResponseEntity.status(HttpStatus.CONFLICT).body(messageSource.getMessage(HttpResponseConstants.USER_UUID_ALREADY_USED_MSG, null, locale));
			}
		}

		raiseLoginAuditEvent(uuid, email, "Login Successful", AuditEventType.PPE_LOGIN_SUCCESS);

		User user = userOptional.get();
		String userInJsonFormat = convertUserToJSON(user);
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

	@ApiOperation(value = "Returns the User Details for the User with matching uuid, email, or patient id")
	@GetMapping(value = "/api/v1/user", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getUser(HttpServletRequest request,
			@ApiParam(value = "Unique Id for the User", required = false) @RequestParam(value = "uuid", required = false) String userUUID,
			@ApiParam(value = "email of the User", required = false) @RequestParam(value = "email", required = false) String email,
			@ApiParam(value = "Patient ID", required = false) @RequestParam(value = "patientId", required = false) String patientId,
			Locale locale) throws JsonProcessingException {
		userUUID = StringUtils.stripToEmpty(userUUID);
		email = StringUtils.stripToEmpty(email);
		patientId = StringUtils.stripToEmpty(patientId);
		return fetchUser(request, userUUID, email, patientId, locale);
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
	@ApiOperation(value = "update the registered user's email notification preference and phone number.")
	@PostMapping(value = "/api/v1/user/{userGUID}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updateUser(HttpServletRequest request,
			@ApiParam(value = "Unique Id for the User", required = true) @PathVariable String userGUID,
			@ApiParam(value = "New phone number for the User", required = true) @RequestParam(value = "phoneNumber", required = true) String phoneNumber,
			@ApiParam(value = "Allow Email Notification or not", required = true) @RequestParam(value = "allowEmailNotification", required = true) Boolean allowEmailNotification,
			@ApiParam(value = "Language preferred by the participant", required = false) @RequestParam(value = "preferredLanguage", required = false) String preferredLanguage,
			Locale locale) throws JsonProcessingException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		String requestingUserUUID = request.getHeader(CommonConstants.HEADER_UUID);
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
	@ApiResponses(value = { @ApiResponse(code = 200, message = "User has been deactivated"),
			@ApiResponse(code = 401, message = "Bearer token is missing or expired.") })
	@ApiOperation(value = "Deactivates a particular user in the portal")
	@PostMapping(value = "/api/v1/deactivate-user/{userUUID}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> deActivateUserByGuid(HttpServletRequest request,
			@ApiParam(value = "Unique Id for the User", required = true) @PathVariable String userUUID, Locale locale)
			throws JsonProcessingException {
		HttpHeaders httpHeaders = createHeader();
		String requestingUserUUID = request.getHeader(CommonConstants.HEADER_UUID);
		userUUID = StringUtils.stripToEmpty(userUUID);

		if (!authService.authorize(requestingUserUUID, userUUID)) {
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
	@ApiOperation(value = "Participant withdraws from the Biobank program")
	@PostMapping(value = "/api/v1/withdraw-user-participation", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> withdrawParticipationByParticipant(HttpServletRequest request,
			@ApiParam(value = "Unique Patient Id assigned to each Patient", required = true) @RequestParam String patientId,
			@ApiParam(value = "List of Questions and their answers for withdrawing from PPE", required = true) @RequestBody List<QuestionAnswerDTO> qsAnsDTO,
			Locale locale) throws JsonProcessingException {

		patientId = StringUtils.stripToEmpty(patientId);
		String updatedByUserUUID = request.getHeader(CommonConstants.HEADER_UUID);

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
		log.info("Request to withdraw Participant " + patient.getPatientId() + " by User " + updatedByUserUUID);
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
		log.info("Patient " + withdrawnPatient.getPatientId() + " new status "
				+ withdrawnPatient.getPortalAccountStatus().getCodeName());
		raiseWithdrawParticipationAuditEvent(patientId, updatedByUserUUID);
		String jsonFormat = convertUserToJSON(withdrawnPatient);
		return new ResponseEntity<>(jsonFormat, httpHeaders, HttpStatus.OK);
	}

	@ApiOperation(value = "CRC will invite a new Patient added from OPEN to participate in the portal by filling in the patient's name and email")
	@PostMapping(value = "/api/v1/user/invite-participant-to-portal", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> inviteParticipant(HttpServletRequest request,
			@ApiParam(value = "Patient Id of the participant", required = true) @RequestParam(value = "patientId", required = true) String patientId,
			Locale locale) throws JsonProcessingException {

		patientId = StringUtils.stripToEmpty(patientId);
		String updatedByUserUUID = request.getHeader(CommonConstants.HEADER_UUID);

		List<String> validAccountStatusList = new ArrayList<>();
		validAccountStatusList.add(PortalAccountStatus.ACCT_NEW.name());
		Optional<User> participantOptional = userService.findByPatientIdAndPortalAccountStatus(patientId,
				validAccountStatusList);
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

	@ApiOperation(value = "CRC will invite a new Patient added from OPEN to participate in the portal by filling in the patient's name and email")
	@PostMapping(value = "/api/v1/user/enter-new-participant-details", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "User invited to PPE Portal"),
			@ApiResponse(code = 401, message = "Not Authorized"),
			@ApiResponse(code = 409, message = "User has already been activated with a different UUID."),
			@ApiResponse(code = 404, message = "User not found"),
			@ApiResponse(code = 406, message = "Language Not Supported") })
	public ResponseEntity<String> enterUserDetails(HttpServletRequest request,
			@ApiParam(value = "Patient Id of the participant", required = true) @RequestParam(value = "patientId", required = true) String patientId,
			@ApiParam(value = "First name of the participant", required = true) @RequestParam(value = "firstName", required = true) String firstName,
			@ApiParam(value = "Last name of the participant", required = true) @RequestParam(value = "lastName", required = true) String lastName,
			@ApiParam(value = "Email Id for the participant", required = true) @RequestParam(value = "emailId", required = true) String emailId,
			@ApiParam(value = "Language preferred by the participant", required = true) @RequestParam(value = "preferredLanguage", required = true) String preferredLanguage,
			Locale locale) throws JsonProcessingException {
		String updatedByUserUUID = request.getHeader(CommonConstants.HEADER_UUID);

		patientId = StringUtils.stripToEmpty(patientId);
		firstName = StringUtils.stripToEmpty(firstName);
		lastName = StringUtils.stripToEmpty(lastName);
		emailId = StringUtils.stripToEmpty(emailId);

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
	 * Convert User or its subclass into its corresponding DTO Object before
	 * creating a JSON string
	 * 
	 * @param user
	 * @return Returns a JSON format string
	 * @throws JsonProcessingException
	 */
	private String convertUserToJSON(User user) throws JsonProcessingException {
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
			if (participantDTO.getCrc().getNotifications() != null) {
				participantDTO.getCrc().getNotifications().clear();
			}
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

	private ResponseEntity<String> fetchUser(HttpServletRequest request, String uuid, String email, String patientId,
			Locale locale) throws JsonProcessingException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		Optional<User> userOptional = Optional.empty();
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
		String requestingUserUUID = request.getHeader(CommonConstants.HEADER_UUID);

		if (!authService.authorize(requestingUserUUID, user)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(httpHeaders)
					.body(messageSource.getMessage(HttpResponseConstants.UNAUTHORIZED_ACCESS, null, locale));
		}

		String userJson = convertUserToJSON(user);
		return new ResponseEntity<>(userJson, httpHeaders, HttpStatus.OK);
	}
}
