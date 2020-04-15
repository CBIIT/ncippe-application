package gov.nci.ppe.services.impl;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nci.ppe.constants.FileType;
import gov.nci.ppe.constants.PPERole;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.Provider;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.services.AuthorizationService;
import gov.nci.ppe.services.UserService;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

	protected Logger logger = Logger.getLogger(AuthorizationService.class.getName());

	@Autowired
	public UserService userService;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean authorize(String requestingUserUUID, User user) {

		// Invalid request no username present
		if (StringUtils.isBlank(requestingUserUUID)) {
			logger.log(Level.WARNING, "No Username present in Request");
			return false;
		}
		// If the UUID in the requester matches the UUID of the targetUser, always allow
		if (requestingUserUUID.equalsIgnoreCase(user.getUserUUID())) {
			logger.log(Level.INFO, "User " + requestingUserUUID + " is requesting access to self");
			return true;
		}

		Optional<User> requesterOpt = userService.findByUuid(requestingUserUUID);
		if (requesterOpt.isEmpty()) {
			logger.log(Level.SEVERE, "No record found for UUID=" + requestingUserUUID);
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
			logger.log(Level.WARNING,
					"Provider " + requestingUserUUID + " denied access to patient " + targetUser.getPatientId());
			return false;
		} else {
			logger.log(Level.INFO,
					"Provider " + requestingUserUUID + " allowed access to patient " + targetUser.getPatientId());
			return true;
		}
	}

	private boolean authorizeCRC(Participant targetUser, final String requestingUserUUID) {
		if (targetUser.getCrc().getUserUUID().equalsIgnoreCase(requestingUserUUID)) {
			logger.log(Level.INFO,
					"CRC " + requestingUserUUID + " allowed access to patient " + targetUser.getPatientId());
			return true;
		} else {
			logger.log(Level.WARNING,
					"CRC " + requestingUserUUID + " denied access to patient " + targetUser.getPatientId());
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
			logger.log(Level.WARNING, "No user found with UUID " + targetUUID);
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
			logger.log(Level.WARNING, "No requesting user found with UUID " + targetPatientId);
			return false;
		}
		User requester = requesterOpt.get();
		Optional<User> targetUserOpt = userService.findActiveParticipantByPatientId(targetPatientId);
		if (targetUserOpt.isEmpty()) {
			logger.log(Level.WARNING, "No requesting user found with UUID " + targetPatientId);
			return false;
		}

		if (FileType.PPE_FILETYPE_ECONSENT_FORM.name().equals(fileType)) {
			// Only the CRC assigned to the patient can upload eConsent Form
			Participant targetUser = (Participant) targetUserOpt.get();
			if (targetUser.getCrc().getUserUUID().equals(requestingUserUUID)) {
				return true;
			} else {
				logger.log(Level.WARNING, "Attempt to upload eConsent Form to Patient ID " + targetPatientId
						+ " by non-authorized user UUID " + requestingUserUUID);
				return false;
			}

		} else if (FileType.PPE_FILETYPE_BIOMARKER_REPORT.name().equals(fileType)) {
			// The Mocha Admin can upload for any active patient
			if (!requester.getRole().getRoleName().equals(PPERole.ROLE_PPE_MOCHA_ADMIN.name())) {
				logger.log(Level.WARNING, "User ID " + requestingUserUUID + " trying to upload Report for Patient ID "
						+ targetPatientId + " without " + PPERole.ROLE_PPE_MOCHA_ADMIN.name() + " role");
				return false;
			} else {

				if (targetUserOpt.isEmpty()) {
					logger.log(Level.WARNING, "No requesting user found with UUID " + targetPatientId);
					return false;
				}
				// If the patient is found, allow upload
				return true;
			}
		}
		// If none of the above file type
		logger.log(Level.WARNING, "Received request to upload unknown file type " + fileType);

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
			logger.log(Level.WARNING, "No requesting user found with UUID " + targetPatientId);
			return false;
		}
		User requester = requesterOpt.get();
		Optional<User> targetUserOpt = userService.findActiveParticipantByPatientId(targetPatientId);
		if (targetUserOpt.isEmpty()) {
			logger.log(Level.WARNING, "No requesting user found with UUID " + targetPatientId);
			return false;
		}
		Participant targetUser = (Participant) targetUserOpt.get();

		// The Patient has access to all files on their own account
		if (requester.getUserUUID() == targetUser.getUserUUID()) {
			logger.log(Level.INFO, "User " + requestingUserUUID + " is requesting access to self");
			return true;
		}

		// CRC can see the eConsent Form and BioMarker reports for their assigned
		// patient.
		if (requestingUserUUID.equals(targetUser.getCrc().getUserUUID())) {
			return true;
		}

		if (FileType.PPE_FILETYPE_BIOMARKER_REPORT.name().equals(fileType)) {
			Optional<Provider> requestingProviderOptional = targetUser.getProviders().stream()
					.filter(provider -> requestingUserUUID.equals(provider.getUserUUID())).findAny();
			if (requestingProviderOptional.isEmpty()) {
				logger.log(Level.WARNING,
						"Provider " + requestingUserUUID + " denied access to patient " + targetUser.getUserUUID());
				return false;
			} else {
				logger.log(Level.INFO,
						"Provider " + requestingUserUUID + " allowed access to patient " + targetUser.getUserUUID());
				return true;
			}
		}

		return false;
	}

}
