package gov.nci.ppe.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.github.dozermapper.core.Mapper;

import gov.nci.ppe.constants.CommonConstants;
import gov.nci.ppe.constants.DatabaseConstants.PortalAccountStatus;
import gov.nci.ppe.constants.PPERole;
import gov.nci.ppe.data.entity.CRC;
import gov.nci.ppe.data.entity.ContentEditor;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.Provider;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.data.entity.dto.ContentEditorDTO;
import gov.nci.ppe.data.entity.dto.CrcDTO;
import gov.nci.ppe.data.entity.dto.JsonViews;
import gov.nci.ppe.data.entity.dto.ParticipantDTO;
import gov.nci.ppe.data.entity.dto.ProviderDTO;
import gov.nci.ppe.data.entity.dto.UserDTO;
import gov.nci.ppe.open.data.entity.dto.OpenResponseDTO;
import gov.nci.ppe.services.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class PrivateApiController {

	protected Logger logger = Logger.getLogger(PrivateApiController.class.getName());
	private static final String NOTIFICATION_GENERATED = "Reminder Notifications Generated";

	@Autowired
	@Qualifier("dozerBean")
	private Mapper dozerBeanMapper;

	@Autowired
	public UserService userService;

	/**
	 * This method will insert new patient, provider and CRC details into PPE
	 * Database if it doesn't exists. The data will be fetched from OPEN.
	 * 
	 * @param openResponseDTO - Patient, Provider and CRC details in JSON Format.
	 * @return - HTTP Response with appropriate message.
	 * @throws JsonProcessingException
	 */
	@ApiOperation(value = "Insert the patient details from OPEN if it doesn't exisit in PPE")
	@PostMapping(value = "/privateapi/v1/user/insert-open-data", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> insertDataFromOpen(
			@ApiParam(value = "JSON Response from OPEN containing patient details", required = true) @RequestBody OpenResponseDTO openResponseDTO)
			throws JsonProcessingException {
		List<User> newUsersList = userService.insertDataFetchedFromOpen(openResponseDTO);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", CommonConstants.APPLICATION_CONTENTTYPE_JSON);
		String jsonFormat = convertUsersToJSON(newUsersList);
		return new ResponseEntity<String>(jsonFormat, httpHeaders, HttpStatus.OK);
	}

	/**
	 * Generate System Notification and Email to remind Users who have not read a
	 * Biomarker report for the specified number of days.
	 * 
	 * @param daysUnread - number of days unread before report is generated.
	 * 
	 * @return
	 */
	@ApiOperation(value = "Generate System Notification and Email to remind Users who have not read a Biomarker report for the specified number of days.")
	@PostMapping(value = "/privateapi/v1/send-reminder", produces = { MediaType.TEXT_PLAIN_VALUE })
	public ResponseEntity<String> generateUnreadReportReminderNotification(
			@ApiParam(value = "Number of days passed since unread report was generated.") @RequestParam(value = "daysUnread", required = true) int daysUnread) {

		userService.generateUnreadReportReminderNotification(daysUnread);
		return ResponseEntity.ok().body(NOTIFICATION_GENERATED);
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
}
