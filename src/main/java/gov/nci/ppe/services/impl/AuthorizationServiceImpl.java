package gov.nci.ppe.services.impl;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nci.ppe.constants.CommonConstants.AuditEventType;
import gov.nci.ppe.constants.FileType;
import gov.nci.ppe.constants.PPERole;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.Provider;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.services.AuditService;
import gov.nci.ppe.services.AuthorizationService;
import gov.nci.ppe.services.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthorizationServiceImpl implements AuthorizationService {

	@Autowired
	private UserService userService;

	@Autowired
	private AuditService auditService;

	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean authorize(String requestingUserUUID, User user) {

		// Invalid request no username present
		if (StringUtils.isBlank(requestingUserUUID)) {
			log.error("No Username present in Request");
			return false;
		}
		// If the UUID in the requester matches the UUID of the targetUser, always allow
		if (requestingUserUUID.equalsIgnoreCase(user.getUserUUID())) {
			log.info("User {} is requesting access to self", requestingUserUUID);
			return true;
		}

		Optional<User> requesterOpt = userService.findByUuid(requestingUserUUID);
		if (requesterOpt.isEmpty()) {
			log.error("No record found for UUID {}", requestingUserUUID);
			return false;
		}

		final String requestingUserRole = requesterOpt.get().getRole().getRoleName();

		switch (PPERole.valueOf(requestingUserRole)) {

		case ROLE_PPE_CRC:
			return authorizeCRC((Participant) user, requestingUserUUID);

		case ROLE_PPE_PROVIDER:
			return authorizeProvider((Participant) user, requestingUserUUID);

		case ROLE_PPE_MOCHA_ADMIN:
			return true;

		default:
			return false;
		}

	}

	private boolean authorizeProvider(Participant targetUser, final String requestingUserUUID) {
		Optional<Provider> requestingProviderOptional = targetUser.getProviders().stream()
				.filter(provider -> requestingUserUUID.equals(provider.getUserUUID())).findAny();
		if (requestingProviderOptional.isEmpty()) {
			raiseAuthorizationEvent(requestingUserUUID, targetUser.getUserUUID(), "Not authorized to access Patient ",
					AuditEventType.PPE_UNAUTHORIZED_ACCESS);
			log.error("Provider " + requestingUserUUID + " denied access to patient " + targetUser.getPatientId());
			return false;
		} else {
			log.info("Provider " + requestingUserUUID + " allowed access to patient " + targetUser.getPatientId());
			return true;
		}

	}

	private boolean authorizeCRC(Participant targetUser, final String requestingUserUUID) {
		if (targetUser.getCrc().getUserUUID().equalsIgnoreCase(requestingUserUUID)) {
			log.info("CRC {} allowed access to patient {} ", requestingUserUUID, targetUser.getPatientId());
			return true;
		} else {
			raiseAuthorizationEvent(requestingUserUUID, targetUser.getUserUUID(), "Not authorized to access Patient ",
					AuditEventType.PPE_UNAUTHORIZED_ACCESS);
			log.error("CRC {} denied access to patient {} ", requestingUserUUID, targetUser.getPatientId());
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean authorize(String requestingUserUUID, String targetUUID) {
		Optional<User> targetUserOptional = userService.findByUuid(targetUUID);
		if (targetUserOptional.isEmpty()) {
			log.error("No user found with UUID " + targetUUID);
			return false;
		} else {
			return authorize(requestingUserUUID, targetUserOptional.get());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean authorizeFileUpload(String requestingUserUUID, String targetPatientId, String fileType) {
		Optional<User> requesterOpt = userService.findByUuid(requestingUserUUID);
		if (requesterOpt.isEmpty()) {
			log.error("No requesting user found with UUID " + targetPatientId);
			return false;
		}
		User requester = requesterOpt.get();
		Optional<User> targetUserOpt = userService.findActiveParticipantByPatientId(targetPatientId);
		if (targetUserOpt.isEmpty()) {
			log.error("No requesting user found with UUID " + targetPatientId);
			raiseAuthorizationEvent(requestingUserUUID, targetPatientId, "Requester Not found",
					AuditEventType.PPE_UNAUTHORIZED_ACCESS);
			return false;
		}

		if (FileType.PPE_FILETYPE_ECONSENT_FORM.name().equals(fileType)) {
			// Only the CRC assigned to the patient can upload eConsent Form
			Participant targetUser = (Participant) targetUserOpt.get();
			if (targetUser.getCrc().getUserUUID().equals(requestingUserUUID)) {
				return true;
			} else {
				log.error("Attempt to upload eConsent Form to Patient ID " + targetPatientId
						+ " by non-authorized user UUID " + requestingUserUUID);
				raiseAuthorizationEvent(requestingUserUUID, targetPatientId,
						"Requester denied access to upload EConsent Form", AuditEventType.PPE_UNAUTHORIZED_ACCESS);

				return false;
			}

		} else if (FileType.PPE_FILETYPE_BIOMARKER_REPORT.name().equals(fileType)) {
			// The Mocha Admin can upload for any active patient
			if (!requester.getRole().getRoleName().equals(PPERole.ROLE_PPE_MOCHA_ADMIN.name())) {
				log.error("User ID " + requestingUserUUID + " trying to upload Report for Patient ID " + targetPatientId
						+ " without " + PPERole.ROLE_PPE_MOCHA_ADMIN.name() + " role");
				raiseAuthorizationEvent(requestingUserUUID, targetPatientId,
						"Requester denied access to upload Biomarker Report", AuditEventType.PPE_UNAUTHORIZED_ACCESS);

				return false;
			} else {
				return true;
			}
		}
		// If none of the above file type
		log.error("Received request to upload unknown file type " + fileType);

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean authorizeFileDownload(String requestingUserUUID, String targetPatientId, String fileType) {
		Optional<User> requesterOpt = userService.findByUuid(requestingUserUUID);
		if (requesterOpt.isEmpty()) {
			log.error("No requesting user found with UUID " + targetPatientId);
			raiseAuthorizationEvent(requestingUserUUID, targetPatientId, "Requester Not found",
					AuditEventType.PPE_UNAUTHORIZED_ACCESS);
			return false;
		}
		User requester = requesterOpt.get();
		Optional<User> targetUserOpt = userService.findActiveParticipantByPatientId(targetPatientId);
		if (targetUserOpt.isEmpty()) {
			log.error("No Patient found with Patient ID " + targetPatientId);
			return false;
		}
		Participant targetUser = (Participant) targetUserOpt.get();

		// The Patient has access to all files on their own account
		if (requester.getUserUUID() == targetUser.getUserUUID()) {
			log.info("User " + requestingUserUUID + " is requesting access to self");
			raiseAuthorizationEvent(requestingUserUUID, targetPatientId,
					"Patient authorized to access file of type " + fileType, AuditEventType.PPE_AUTHORIZATION_SUCCESS);
			return true;
		}

		// CRC can see the eConsent Form and BioMarker reports for their assigned
		// patient.
		if (requestingUserUUID.equals(targetUser.getCrc().getUserUUID())) {
			raiseAuthorizationEvent(requestingUserUUID, targetPatientId,
					"CRC authorized to access file of type " + fileType, AuditEventType.PPE_AUTHORIZATION_SUCCESS);
			return true;
		}

		Optional<Provider> requestingProviderOptional = targetUser.getProviders().stream()
				.filter(provider -> requestingUserUUID.equals(provider.getUserUUID())).findAny();
		if (requestingProviderOptional.isEmpty()) {
			log.error("Provider " + requestingUserUUID + " denied access to patient file " + targetUser.getUserUUID());
			raiseAuthorizationEvent(requestingUserUUID, targetPatientId,
					"Provider denied to access file of type " + fileType, AuditEventType.PPE_UNAUTHORIZED_ACCESS);
			return false;
		} else {
			log.info("Provider " + requestingUserUUID + " allowed access to patient file " + targetUser.getUserUUID());
			raiseAuthorizationEvent(requestingUserUUID, targetPatientId,
					"Provider authorized to access file of type " + fileType, AuditEventType.PPE_AUTHORIZATION_SUCCESS);
			return true;
		}
	}

	/**
	 * Raises an Audit Event
	 * 
	 * @param requester - UUID of the user requesting the authorization
	 * @param target    - UUID of the target the requester is requesting
	 *                  authorization for
	 * @param notes     - Details of the Audit event
	 * @param eventType - The {@link AuditEventType} of the Audit event
	 * @throws JsonProcessingException
	 */
	private void raiseAuthorizationEvent(String requester, String target, String notes, AuditEventType eventType) {
		ObjectNode auditDetail = mapper.createObjectNode();
		auditDetail.put("Requester UUID", requester);
		auditDetail.put("Target User UUID", target);
		auditDetail.put("Notes", notes);

		try {
			auditService.logAuditEvent(auditDetail, eventType);
		} catch (JsonProcessingException e) {
			log.error("Internal Error", e);
		}

	}
}
