package gov.nci.ppe.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
import gov.nci.ppe.services.JWTManagementService;
import gov.nci.ppe.services.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller class for User related actions.
 * 
 * @author PublicisSapient
 * @version 1.0;
 * @since 2019-07-22
 */

@RestController
public class UserController {

	protected Logger logger = Logger.getLogger(UserController.class.getName());

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
	private JWTManagementService jwtMgmtService;

	@Autowired
	AuthorizationService authService;

	private final String AUTHORIZATION = "Authorization";
	private final String UNAUTHORIZED_ACCESS = "{\n\"error\" : \"Not authorized to access the requested data \"\n}";
	private final String NO_USER_FOUND_MSG = "{\n\"error\" : \"No User found \"\n}";
	private final String INACTIVE_USER_MSG = "{\n\"error\" : \"User is in Inactive Status \"\n}";
	private final String USER_UUID_ALREADY_USED_MSG = "{\n\"error\" : \"The specified UUID is already associated with an existing user\"\n}";
	private final String HEADER_UUID = "sm_user";
	private final String HEADER_EMAIL = "user_email";
	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * This method will return all the registered users along with their roles. The
	 * values included in the JSON response is mentioned in UserDTP
	 * 
	 * @return JSON Response with HTTP status 200 or 204 if no content is available.
	 * @throws JsonProcessingException
	 */
	@ApiOperation(value = "Return all the registered users along with their roles.")
	@GetMapping(value = "/api/v1/users", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAllRegisteredUsers() throws JsonProcessingException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", CommonConstants.APPLICATION_CONTENTTYPE_JSON);
		List<User> userList = userService.getAllRegisteredUsers();
		// If there are no registered users, return back no content
		if (CollectionUtils.isEmpty(userList)) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(NO_USER_FOUND_MSG);
		}

		String responseString = convertUsersToJSON(userList);
		return new ResponseEntity<String>(responseString, httpHeaders, HttpStatus.OK);
	}

	/**
	 * This method will generate a JWT token for the logged in user
	 * 
	 * @param uuid
	 * @param email
	 * @return a JWT Token
	 * @throws JsonProcessingException
	 */
	@ApiResponses(value = { @ApiResponse(code = 200, message = "User logged in successfully"),
			@ApiResponse(code = 409, message = "User has already been activated with a different UUID."),
			@ApiResponse(code = 404, message = "User not found") })
	@PostMapping(value = "/api/v1/login", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> login(@RequestParam(value = "uuid", required = true) String uuid,
			@RequestParam(value = "email", required = true) String email) throws JsonProcessingException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", CommonConstants.APPLICATION_CONTENTTYPE_JSON);
		List<String> accountStatusList = new ArrayList<String>();
		accountStatusList.add(PortalAccountStatus.ACCT_ACTIVE.name());

		uuid = StringUtils.stripToEmpty(uuid);
		email = StringUtils.stripToEmpty(email);

		Optional<User> userOptional = userService.findByUuidAndPortalAccountStatus(uuid, accountStatusList);
		if (userOptional.isEmpty()) {
			userOptional = userService.activateUser(email, uuid);

			if (!userOptional.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NO_USER_FOUND_MSG);
			}

			User user = userOptional.get();
			if (!user.getUserUUID().equalsIgnoreCase(uuid)) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(USER_UUID_ALREADY_USED_MSG);
			}
		}

		User user = userOptional.get();

		String jwt = jwtMgmtService.createJWT(user);
		raiseLoginAuditEvent(uuid, email);
		ObjectNode responseJsonWithToken = mapper.createObjectNode();
		responseJsonWithToken.put("token", jwt);
		return new ResponseEntity<String>(mapper.writeValueAsString(responseJsonWithToken), httpHeaders, HttpStatus.OK);
	}

	@GetMapping(value = "/api/v1/user", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getSelfData(HttpServletRequest request,
			@RequestHeader MultiValueMap<String, String> headers) throws JsonProcessingException {

		logger.info("Request UUID: " + request.getHeader(HEADER_UUID));
		logger.info("Request EMAIL: " + request.getHeader(HEADER_EMAIL));
		String uuid = getFromHeader(headers, HEADER_UUID);
		String email = getFromHeader(headers, HEADER_EMAIL);
		logger.info("Getting User data for Self: UUID=" + uuid + ", EMAIL=" + email);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", CommonConstants.APPLICATION_CONTENTTYPE_JSON);
		List<String> accountStatusList = new ArrayList<String>();
		accountStatusList.add(PortalAccountStatus.ACCT_ACTIVE.name());

		Optional<User> userOptional = userService.findByUuidAndPortalAccountStatus(uuid, accountStatusList);
		if (userOptional.isEmpty()) {
			userOptional = userService.activateUser(email, uuid);

			if (!userOptional.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NO_USER_FOUND_MSG);
			}

			User user = userOptional.get();
			if (!user.getUserUUID().equalsIgnoreCase(uuid)) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(USER_UUID_ALREADY_USED_MSG);
			}
		}

		User user = userOptional.get();
		String userInJsonFormat = convertUserToJSON(user);
		return new ResponseEntity<String>(mapper.writeValueAsString(userInJsonFormat), httpHeaders, HttpStatus.OK);


	}

	private String getFromHeader(MultiValueMap<String, String> headers, String key) {
		return headers.getFirst(key);
	}

	/**
	 * This method will generate the JSON response based on the user.
	 * 
	 * @param userUUID - Unique Id for the User
	 * @return a JSON Response
	 * @throws JsonProcessingException
	 */
	@ApiOperation(value = "Returns the User Details for the specified User")
	@GetMapping(value = "/api/v1/user/{userUUID}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getUser(HttpServletRequest request,
			@ApiParam(value = "Unique Id for the User", required = true) @PathVariable String userUUID)
			throws JsonProcessingException {
		userUUID = StringUtils.stripToEmpty(userUUID);
		return fetchUser(request, userUUID, null, null);
	}

	@ApiOperation(value = "Returns the User Details for the User with matching uuid, email, or patient id")
	@GetMapping(value = "/api/v1/patient", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getUser(HttpServletRequest request,
			@ApiParam(value = "Unique Id for the User", required = false) @RequestParam(value = "uuid", required = false) String userUUID,
			@ApiParam(value = "email of the User", required = false) @RequestParam(value = "email", required = false) String email,
			@ApiParam(value = "Patient ID", required = false) @RequestParam(value = "patientId", required = false) String patientId)
			throws JsonProcessingException {
		userUUID = StringUtils.stripToEmpty(userUUID);
		email = StringUtils.stripToEmpty(email);
		patientId = StringUtils.stripToEmpty(patientId);
		return fetchUser(request, userUUID, email, patientId);
	}

	/**
	 * This method will update the registered user's email notification preference
	 * and phone number.
	 * 
	 * @param userGUID               - GUID of the user
	 * @param phoneNumber
	 * @param allowEmailNotification
	 * @return
	 * @throws JsonProcessingException
	 */
	@ApiOperation(value = "update the registered user's email notification preference and phone number.")
	@PutMapping(value = "/api/v1/user/{userGUID}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updateUser(HttpServletRequest request,
			@ApiParam(value = "Unique Id for the User", required = true) @PathVariable String userGUID,
			@ApiParam(value = "New phone number for the User", required = true) @RequestParam(value = "phoneNumber", required = true) String phoneNumber,
			@ApiParam(value = "Allow Email Notification or not", required = true) @RequestParam(value = "allowEmailNotification", required = true) Boolean allowEmailNotification,
			@ApiParam(value = "Language preferred by the participant", required = false) @RequestParam(value = "preferredLanguage", required = false) String preferredLanguage)
			throws JsonProcessingException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", CommonConstants.APPLICATION_CONTENTTYPE_JSON);

		String value = request.getHeader(AUTHORIZATION);
		if (!authService.authorize(value, userGUID)) {
			return new ResponseEntity<String>(UNAUTHORIZED_ACCESS, httpHeaders, HttpStatus.UNAUTHORIZED);
		}

		userGUID = StringUtils.stripToEmpty(userGUID);
		phoneNumber = StringUtils.stripToEmpty(phoneNumber);

		LanguageOption preferredLang = null;

		try {
			preferredLang = LanguageOption.getLanguageOption(preferredLanguage);
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body("Invalid Language Option selected : " + preferredLanguage);
		}

		Optional<User> userOptional = userService.updateUserDetails(userGUID, allowEmailNotification, phoneNumber,
				preferredLang);
		if (!userOptional.isPresent()) {
			return new ResponseEntity<String>(NO_USER_FOUND_MSG, httpHeaders, HttpStatus.NO_CONTENT);
		}

		User user = userOptional.get();
		String jsonFormat = convertUserToJSON(user);

		return new ResponseEntity<String>(jsonFormat, httpHeaders, HttpStatus.OK);
	}

	/**
	 * Method to deactivate/close online account for the user.
	 * 
	 * @param request  - HTTPRequest object
	 * @param userUUID - uuid for the user who is deactivating the account.
	 * @return
	 * @throws JsonProcessingException
	 */
	@ApiResponses(value = { @ApiResponse(code = 200, message = "User has been deactivated"),
			@ApiResponse(code = 401, message = "Bearer token is missing or expired.") })
	@ApiOperation(value = "Deactivates a particular user in the portal")
	@PatchMapping(value = "/api/v1/deactivate-user/{userUUID}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> deActivateUserByGuid(HttpServletRequest request,
			@ApiParam(value = "Unique Id for the User", required = true) @PathVariable String userUUID)
			throws JsonProcessingException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", CommonConstants.APPLICATION_CONTENTTYPE_JSON);
		String value = request.getHeader(AUTHORIZATION);
		userUUID = StringUtils.stripToEmpty(userUUID);

		if (!authService.authorize(value, userUUID)) {
			return new ResponseEntity<String>(UNAUTHORIZED_ACCESS, httpHeaders, HttpStatus.UNAUTHORIZED);
		}

		Optional<User> userOptional = userService.deactivateUserPortalAccountStatus(userUUID);
		String jsonFormat = convertUserToJSON(userOptional.get());
		return new ResponseEntity<String>(jsonFormat, httpHeaders, HttpStatus.OK);
	}

	/**
	 * This method will authorize an user who has logged in via login.gov
	 * 
	 * @param request - the HTTP Request object
	 * @param email   - the email used by the user to login to Login.gov
	 * @return
	 * @throws JsonProcessingException
	 */
	@ApiOperation(value = "Authorize a user who has logged in via login.gov")
	@PatchMapping(value = "/api/v1/authorize-user", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> authorizeUser(HttpServletRequest request,
			@ApiParam(value = "The UUID for the patient", required = true) @RequestParam String uuid,
			@ApiParam(value = "The email used by the user to login to Login.gov", required = true) @RequestParam String email)
			throws JsonProcessingException {

		uuid = StringUtils.stripToEmpty(uuid);
		email = StringUtils.stripToEmpty(email);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", CommonConstants.APPLICATION_CONTENTTYPE_JSON);
		List<String> validAccountStatusList = new ArrayList<>();
		validAccountStatusList.add(PortalAccountStatus.ACCT_INITIATED.name());
		validAccountStatusList.add(PortalAccountStatus.ACCT_ACTIVE.name());

		Optional<User> userOptional = userService.authorizeUser(email, uuid);
		if (!userOptional.isPresent()) {
			logger.info(" No User found for the search criteria : uuid = " + uuid + "  and email=" + email);
			return new ResponseEntity<String>(NO_USER_FOUND_MSG, httpHeaders, HttpStatus.NOT_FOUND);
		}

		User user = userOptional.get();

		// Check if the user is active
		if (!(PortalAccountStatus.ACCT_INITIATED.name().equalsIgnoreCase(user.getPortalAccountStatus().getCodeName())
				|| PortalAccountStatus.ACCT_ACTIVE.name()
						.equalsIgnoreCase(user.getPortalAccountStatus().getCodeName()))) {
			logger.info(" Inactive user found for the search criteria : uuid = " + uuid + "  and email=" + email);
			return new ResponseEntity<String>(INACTIVE_USER_MSG, httpHeaders, HttpStatus.UNAUTHORIZED);
		}

		String responseJsonWithToken = jwtMgmtService.createJWT(user);
		raiseLoginAuditEvent(uuid, email);
		return ResponseEntity.ok(responseJsonWithToken);
	}

	/**
	 * This method will allow a participant to withdraw from the program. A CRC can
	 * also withdraw a participant from the program.
	 * 
	 * @param request       - HTTPRequest object
	 * @param patientId     - Unique Patient Id assigned to each Patient
	 * @param updatedByUser - UUID of the user responsible for withdrawing the
	 *                      participant
	 * @param qsAnsDTO      - List of Questions and Answers
	 * @return - HTTP Response with appropriate message.
	 * @throws JsonProcessingException
	 */
	@ApiOperation(value = "Participant withdraws from the Biobank program")
	@PostMapping(value = "/api/v1/withdraw-user-participation", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> withdrawParticipationByParticipant(HttpServletRequest request,
			@ApiParam(value = "Unique Patient Id assigned to each Patient", required = true) @RequestParam String patientId,
			@ApiParam(value = "UUID for the User performing this action", required = true) @RequestParam(value = "updatedByUser", required = true) String updatedByUser,
			@ApiParam(value = "List of Questions and their answers for withdrawing from PPE", required = true) @RequestBody List<QuestionAnswerDTO> qsAnsDTO)
			throws JsonProcessingException {

		patientId = StringUtils.stripToEmpty(patientId);
		updatedByUser = StringUtils.stripToEmpty(updatedByUser);

		Optional<User> participantOptional = userService.findActiveParticipantByPatientId(patientId);
		if (!participantOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NO_USER_FOUND_MSG);
		}
		Participant patient = (Participant) participantOptional.get();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", CommonConstants.APPLICATION_CONTENTTYPE_JSON);
		String value = request.getHeader(AUTHORIZATION);
		if (!authService.authorize(value, patient)) {
			return new ResponseEntity<String>(UNAUTHORIZED_ACCESS, httpHeaders, HttpStatus.UNAUTHORIZED);
		}

		Code code = codeService.getCode(QuestionAnswerType.PPE_WITHDRAW_SURVEY_QUESTION.getQuestionAnswerType());
		List<QuestionAnswer> qsAnsList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(qsAnsDTO)) {
			qsAnsDTO.forEach(qs -> {
				QuestionAnswer questionAnswer = dozerBeanMapper.map(qs, QuestionAnswer.class);
				questionAnswer.setDateAnswered(new Timestamp(System.currentTimeMillis()));
				questionAnswer.setParticipantForQA(patient);
				questionAnswer.setQuestionCategory(code);
				qsAnsList.add(questionAnswer);
			});
		}
		/*
		 * Find out if the participant withdrew themselves or the CRC did it for them
		 * and set the LastRevisedUser accordingly
		 */
		if (StringUtils.equals(patient.getUserUUID(), updatedByUser)) {
			patient.setLastRevisedUser(patient.getUserId());
		} else {
			Optional<User> crcOptional = userService.findByUuid(updatedByUser);
			if (!crcOptional.isEmpty()) {
				patient.setLastRevisedUser(crcOptional.get().getUserId());
			}
		}
		Optional<User> userOptional = userService.withdrawParticipationFromBiobankProgramAndSendNotification(patient,
				qsAnsList);
		raiseWithdrawParticipationAuditEvent(patientId, updatedByUser);
		String jsonFormat = convertUserToJSON(userOptional.get());
		return new ResponseEntity<String>(jsonFormat, httpHeaders, HttpStatus.OK);
	}

	@ApiOperation(value = "CRC will invite a new Patient added from OPEN to participate in the portal by filling in the patient's name and email")
	@PatchMapping(value = "/api/v1/user/invite-participant-to-portal", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> inviteParticipant(HttpServletRequest request,
			@ApiParam(value = "UUID for the User performing this action", required = true) @RequestParam(value = "updatedByUser", required = true) String updatedByUser,
			@ApiParam(value = "Patient Id of the participant", required = true) @RequestParam(value = "patientId", required = true) String patientId)
			throws JsonProcessingException {

		patientId = StringUtils.stripToEmpty(patientId);
		updatedByUser = StringUtils.stripToEmpty(updatedByUser);


		List<String> validAccountStatusList = new ArrayList<>();
		validAccountStatusList.add(PortalAccountStatus.ACCT_NEW.name());
		Optional<User> participantOptional = userService.findByPatientIdAndPortalAccountStatus(patientId,
				validAccountStatusList);
		if (participantOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NO_USER_FOUND_MSG);
		}

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", CommonConstants.APPLICATION_CONTENTTYPE_JSON);
		String value = request.getHeader(AUTHORIZATION);
		if (!authService.authorize(value, participantOptional.get())) {
			return new ResponseEntity<String>(UNAUTHORIZED_ACCESS, httpHeaders, HttpStatus.UNAUTHORIZED);
		}

		participantOptional = userService.invitePatientToPortal(patientId, updatedByUser);

		String jsonFormat = convertUserToJSON(participantOptional.get());
		return new ResponseEntity<String>(jsonFormat, httpHeaders, HttpStatus.OK);
	}


	@ApiOperation(value = "CRC will invite a new Patient added from OPEN to participate in the portal by filling in the patient's name and email")
	@PatchMapping(value = "/api/v1/user/enter-new-participant-details", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> enterUserDetails(HttpServletRequest request,
			@ApiParam(value = "UUID for the User performing this action", required = true) @RequestParam(value = "updatedByUser", required = true) String updatedByUser,
			@ApiParam(value = "Patient Id of the participant", required = true) @RequestParam(value = "patientId", required = true) String patientId,
			@ApiParam(value = "First name of the participant", required = true) @RequestParam(value = "firstName", required = true) String firstName,
			@ApiParam(value = "Last name of the participant", required = true) @RequestParam(value = "lastName", required = true) String lastName,
			@ApiParam(value = "Email Id for the participant", required = true) @RequestParam(value = "emailId", required = true) String emailId,
			@ApiParam(value = "Language preferred by the participant", required = true) @RequestParam(value = "preferredLanguage", required = true) String preferredLanguage)
			throws JsonProcessingException {
		patientId = StringUtils.stripToEmpty(patientId);
		updatedByUser = StringUtils.stripToEmpty(updatedByUser);
		firstName = StringUtils.stripToEmpty(firstName);
		lastName = StringUtils.stripToEmpty(lastName);
		emailId = StringUtils.stripToEmpty(emailId);

		LanguageOption preferredLang = null;

		try {
			preferredLang = LanguageOption.getLanguageOption(preferredLanguage);
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body("Invalid Language Option selected : " + preferredLanguage);
		}

		List<String> validAccountStatusList = new ArrayList<>();
		validAccountStatusList.add(PortalAccountStatus.ACCT_NEW.name());
		Optional<User> participantOptional = userService.findByPatientIdAndPortalAccountStatus(patientId,
				validAccountStatusList);
		if (participantOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NO_USER_FOUND_MSG);
		}

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", CommonConstants.APPLICATION_CONTENTTYPE_JSON);
		String value = request.getHeader(AUTHORIZATION);
		User newPatient = participantOptional.get();
		if (!authService.authorize(value, newPatient)) {
			return new ResponseEntity<String>(UNAUTHORIZED_ACCESS, httpHeaders, HttpStatus.UNAUTHORIZED);
		}

		// Fill in the details
		newPatient.setFirstName(firstName);
		newPatient.setLastName(lastName);
		newPatient.setEmail(emailId);
		newPatient.setPreferredLanguage(preferredLang);

		Optional<User> optionalCRC = authService.getRequestingUser(value);
		// No need to check for existence, as otherwise authorize() call would have
		// failed
		newPatient.setLastRevisedUser(optionalCRC.get().getLastRevisedUser());
		participantOptional = userService.updateUser(newPatient);

		String jsonFormat = convertUserToJSON(participantOptional.get());
		return new ResponseEntity<String>(jsonFormat, httpHeaders, HttpStatus.OK);
	}

	/*
	 * General method to convert a Collection of User object into UserDTO object and
	 * return the String in JSON format
	 */
	private String convertUsersToJSON(List<User> userList) throws JsonProcessingException {
		List<UserDTO> userDTOList = new ArrayList<UserDTO>();
		userList.forEach(usr -> {
			userDTOList.add(convertUserDTO(usr));
		});
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerSubtypes(new NamedType(ParticipantDTO.class, "ParticipantDTO"),
				new NamedType(ProviderDTO.class, "ProviderDTO"), new NamedType(CrcDTO.class, "CrcDTO"));

		return mapper.writerWithView(JsonViews.UsersSummaryView.class).writeValueAsString(userDTOList);

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
		ObjectMapper mapper = new ObjectMapper();
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
			participantDTO.getProviders()
					.forEach(associatedProvider -> {
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

	private void raiseLoginAuditEvent(String uuid, String email) throws JsonProcessingException {
		ObjectNode auditDetail = mapper.createObjectNode();

		auditDetail.put("UUID", uuid).put("email", email);
		String auditDetailString = mapper.writeValueAsString(auditDetail);
		auditService.logAuditEvent(auditDetailString, AuditEventType.PPE_LOGIN.name());
	}

	private void raiseWithdrawParticipationAuditEvent(String patientId, String uuid) throws JsonProcessingException {
		ObjectNode auditDetail = mapper.createObjectNode();

		auditDetail.put("UUID", uuid).put("PatientID", patientId);
		String auditDetailString = mapper.writeValueAsString(auditDetail);
		auditService.logAuditEvent(auditDetailString, AuditEventType.PPE_WITHDRAW_FROM_PROGRAM.name());
	}

	private ResponseEntity<String> fetchUser(HttpServletRequest request, String uuid, String email, String patientId)
			throws JsonProcessingException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", CommonConstants.APPLICATION_CONTENTTYPE_JSON);
		Optional<User> userOptional = null;
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
			return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(httpHeaders).body(NO_USER_FOUND_MSG);
		}
		User user = userOptional.get();
		String authToken = request.getHeader(AUTHORIZATION);
		logger.finest("TOKEN #########" + authToken);
		if (!authService.authorize(authToken, user)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).headers(httpHeaders).body(UNAUTHORIZED_ACCESS);
		}

		String userJson = convertUserToJSON(user);
		return new ResponseEntity<String>(userJson, httpHeaders, HttpStatus.OK);
	}
}
