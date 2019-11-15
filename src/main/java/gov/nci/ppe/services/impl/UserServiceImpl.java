package gov.nci.ppe.services.impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nci.ppe.configurations.EmailServiceConfig;
import gov.nci.ppe.configurations.NotificationServiceConfig;
import gov.nci.ppe.constants.CommonConstants.AuditEventType;
import gov.nci.ppe.constants.DatabaseConstants.PortalAccountStatus;
import gov.nci.ppe.constants.DatabaseConstants.UserType;
import gov.nci.ppe.constants.FileType;
import gov.nci.ppe.constants.PPERole;
import gov.nci.ppe.data.entity.CRC;
import gov.nci.ppe.data.entity.Code;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.Provider;
import gov.nci.ppe.data.entity.QuestionAnswer;
import gov.nci.ppe.data.entity.Role;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.data.repository.CodeRepository;
import gov.nci.ppe.data.repository.ParticipantRepository;
import gov.nci.ppe.data.repository.QuestionAnswerRepository;
import gov.nci.ppe.data.repository.RoleRepository;
import gov.nci.ppe.data.repository.UserRepository;
import gov.nci.ppe.services.AuditService;
import gov.nci.ppe.services.EmailLogService;
import gov.nci.ppe.services.NotificationService;
import gov.nci.ppe.services.UserService;

/**
 * This is a Service class that orchestrates all the calls to entities and
 * returns JSON responses.
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-07-22
 */
@Component
public class UserServiceImpl implements UserService {

	private UserRepository userRepository;

	private CodeRepository codeRepository;
	
	private RoleRepository roleRepository;

	private ParticipantRepository participantRepository;

	private QuestionAnswerRepository qsAnsRepo;

	@Autowired
	public EmailLogService emailService;

	@Autowired
	private EmailServiceConfig emailServiceConfig;

	@Autowired
	private NotificationServiceConfig notificationServiceConfig;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private AuditService auditService;

	private ObjectMapper mapper = new ObjectMapper();

	public UserServiceImpl() {
	}

	@Autowired
	public UserServiceImpl(UserRepository userRepo, CodeRepository codeRepo, ParticipantRepository participantRepo,
			QuestionAnswerRepository qsAnsRepo, RoleRepository _roleRepository) {
		super();
		this.userRepository = userRepo;
		this.codeRepository = codeRepo;
		this.participantRepository = participantRepo;
		this.qsAnsRepo = qsAnsRepo;
		this.roleRepository = _roleRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<User> getAllRegisteredUsers() {
		return userRepository.findAll();
	}

	@Override
	public Optional<User> findByUuid(String userGuid) {
		Optional<User> optionalUser = userRepository.findByUserUUID(userGuid);
		return updateAssociatedPatientRecords(optionalUser);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> updateUserDetails(String userGuid, Boolean userEmailNotification, String phoneNumber) {
		Optional<User> userOptional = userRepository.findByUserUUID(userGuid);

		/* Check if the user is present in the system */
		if (!userOptional.isPresent()) {
			return userOptional;
		}

		User user = userOptional.get();
		user.setAllowEmailNotification(userEmailNotification);
		user.setPhoneNumber(phoneNumber);
		user.setLastRevisedUser(user.getUserId());
		user.setLastRevisedDate(new Timestamp(System.currentTimeMillis()));
		User updateUser = userRepository.save(user);

		return Optional.of(updateUser);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> findUserById(Long userId) {
		return userRepository.findById(userId);
	}

	/**
	 * This method clears the patient notifications and marks if the patient has any
	 * reports not viewed by the CRC/Provider
	 * 
	 * @param associatedPatients
	 * @param viewingUser
	 * @return
	 */
	private Set<Participant> updateAssociatedPatientRecordsForCRCandProvider(Set<Participant> associatedPatients,
			User viewingUser) {
		associatedPatients.forEach(patient -> {
			patient.getNotifications().clear();
			patient.getReports().forEach(report -> {
				if (report.getFileType().getCodeName()
						.equalsIgnoreCase(FileType.PPE_FILETYPE_BIOMARKER_REPORT.getFileType())
						&& !report.getViewedBy().contains(viewingUser)) {
					patient.setHasNewReports(true);
				}
			});
		});
		return associatedPatients;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> activateUser(String userEmail, String userUUID) {
		Optional<User> optionalUser = userRepository.findByEmail(userEmail);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			if (StringUtils.isBlank(user.getUserUUID())) {
				// Fill in the user's UUID and save.
				user.setUserUUID(userUUID);
				user = userRepository.save(user);
				optionalUser = Optional.of(user);
			}
		}
		return optionalUser;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> updateEmail(String userUUID, String userEmail) {
		Optional<User> optionalUser = userRepository.findByUserUUID(userUUID);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			user.setEmail(userEmail);
			user = userRepository.save(user);
			optionalUser = Optional.of(user);
		}
		return optionalUser;
	}

	@Override
	public Optional<User> findByUuidAndPortalAccountStatus(String userGuid, List<String> accountStatusList) {
		List<Code> accountStatusCodeList = convertToCode(accountStatusList);
		Optional<User> optionalUser = userRepository.findByUserUUIDAndPortalAccountStatusIn(userGuid,
				accountStatusCodeList);
		return updateAssociatedPatientRecords(optionalUser);
	}

	private Optional<User> updateAssociatedPatientRecords(Optional<User> optionalUser) {
		if (optionalUser.isPresent()) {
			// For Providers and CRC admins, check if each associated participant has
			// reports that the CRC/Provider has not seen.
			Set<Participant> associatedPatients = null;
			User user = optionalUser.get();
			if (null != user.getRole()) {
				if (user.getRole().getRoleName().equalsIgnoreCase(PPERole.ROLE_CRC.getRoleName())) {
					associatedPatients = ((CRC) user).getPatients();

				} else if (user.getRole().getRoleName().equalsIgnoreCase(PPERole.ROLE_PROVIDER.getRoleName())) {
					associatedPatients = ((Provider) user).getPatients();
				}
			}
			if (associatedPatients != null && !associatedPatients.isEmpty()) {
				updateAssociatedPatientRecordsForCRCandProvider(associatedPatients, user);
			}
		}
		return optionalUser;
	}

	private List<Code> convertToCode(List<String> accountStatusList) {
		List<Code> accountStatusCodeList = codeRepository.findByCodeNameIn(accountStatusList);
		List<Long> accountStatusCodeIdList = new ArrayList<Long>();
		accountStatusCodeList.forEach(code -> accountStatusCodeIdList.add(code.getCodeId()));
		return accountStatusCodeList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> deactivateUserPortalAccountStatus(String userUUID) {
		Optional<User> userOptional = userRepository.findByUserUUID(userUUID);
		if (!userOptional.isPresent()) {
			return userOptional;
		}
		Code portalAccountStatusCode = codeRepository.findByCodeName(PortalAccountStatus.ACCT_TERMINATED_AT_PPE.name());
		User user = userOptional.get();
		user.setPortalAccountStatus(portalAccountStatusCode);
		user.setLastRevisedUser(user.getUserId());
		user.setAllowEmailNotification(false);
		user.setLastRevisedDate(new Timestamp(System.currentTimeMillis()));
		User updateUser = userRepository.save(user);
		return Optional.of(updateUser);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> withdrawParticipationFromBiobankProgram(Participant patient, List<QuestionAnswer> qsAnsList) {
		Timestamp qsAnsInsertionTime = new Timestamp(System.currentTimeMillis());
		qsAnsList.forEach(qs -> {
			qs.setDateAnswered(qsAnsInsertionTime);
		});
		qsAnsRepo.saveAll(qsAnsList);
		patient.setLastRevisedDate(qsAnsInsertionTime);
		patient.setIsActiveBiobankParticipant(false);
		patient.setDateDeactivated(qsAnsInsertionTime);
		return Optional.of(userRepository.save(patient));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> authorizeUser(String email, String uuid) {
		boolean userUpdatedFlag = false;
		Timestamp updatedTime = new Timestamp(System.currentTimeMillis());
		Optional<User> userOptional = findByUuid(uuid);
		if (userOptional.isEmpty()) {
			// No uuid match. Could be a first time user. Search by email.
			userOptional = findByEmail(email);
			if (!userOptional.isPresent() || StringUtils.isNotBlank(userOptional.get().getUserUUID())) {

				return Optional.empty();
			}
		}

		User user = userOptional.get();

		// Update the email id if they don't match
		if (!StringUtils.equalsIgnoreCase(email, user.getEmail())) {
			user.setEmail(email);
			userUpdatedFlag = true;
		}

		// activate the user if they are not already done
		if (StringUtils.isAllBlank(user.getUserUUID())) {
			user.setUserUUID(uuid);
			user.setPortalAccountStatus(codeRepository.findByCodeName(PortalAccountStatus.ACCT_ACTIVE.name()));
			user.setDateActivated(updatedTime);
			userUpdatedFlag = true;
		}

		// Update the record in the database if there are any changes
		if (userUpdatedFlag) {
			user.setLastRevisedDate(updatedTime);
			user.setLastRevisedUser(user.getUserId());
			Optional<User> updatedUserOptional = updateUser(user);
			user = updatedUserOptional.get();
		}

		return Optional.of(user);

	}

	@Override
	public String decryptLoginGovToken(String idToken) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> findByEmailAndPortalAccountStatus(String email, List<String> validAccountStatusList) {
		List<Code> accountStatusCodeList = convertToCode(validAccountStatusList);
		Optional<User> optionalUser = userRepository.findByEmailAndPortalAccountStatusIn(email, accountStatusCodeList);
		return updateAssociatedPatientRecords(optionalUser);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> findByPatientIdAndPortalAccountStatus(String patientId, List<String> validAccountStatusList) {
		List<Code> accountStatusCodeList = convertToCode(validAccountStatusList);
		Optional<User> optionalUser = participantRepository.findByPatientIdAndPortalAccountStatusIn(patientId,
				accountStatusCodeList);
		return optionalUser;
	}

	@Override
	public Optional<User> findActiveParticipantByPatientId(String patientId) {
		return participantRepository.findByPatientIdAndIsActiveBiobankParticipantTrue(patientId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> withdrawParticipationFromBiobankProgramAndSendNotification(Participant patient,
			List<QuestionAnswer> qsAnsList) {
		Optional<User> userOptional = withdrawParticipationFromBiobankProgram(patient, qsAnsList);
		Participant withdrawnPatient = (Participant) userOptional.get();
		StringBuilder questionAnswers = new StringBuilder();
		withdrawnPatient.getQuestionAnswers().forEach(qs -> {
			questionAnswers.append("\u2022").append(" ").append(qs.getQuestion()).append(" : ").append(qs.getAnswer())
					.append("<br/>");
		});
		if (patient.getUserId() == patient.getLastRevisedUser()) {
			if (withdrawnPatient.getCRC().getAllowEmailNotification()) {
				sendEmailToCRCAfterParticipantWithdraws(withdrawnPatient.getFirstName(), withdrawnPatient.getLastName(),
						withdrawnPatient.getCRC().getFirstName(), withdrawnPatient.getCRC().getEmail(),
						questionAnswers.toString());
			}
			if (PortalAccountStatus.ACCT_ACTIVE.name()
					.equalsIgnoreCase(withdrawnPatient.getPortalAccountStatus().getCodeName())
					|| PortalAccountStatus.ACCT_INITIATED.name()
							.equalsIgnoreCase(withdrawnPatient.getPortalAccountStatus().getCodeName())) {
				String notificationTitle = notificationServiceConfig.getParticipantWithdrawsSelfSubject()
						.concat(StringUtils.CR) + LocalDate.now();
				notificationTitle = StringUtils.replace(notificationTitle, "%{FullName}",
						withdrawnPatient.getFullName());
				notificationService.addNotification(notificationServiceConfig.getParticipantWithdrawsSelfFrom(),
						notificationTitle, notificationServiceConfig.getParticipantWithdrawsSelfMessage(),
						withdrawnPatient.getCRC().getCrcId(), withdrawnPatient.getFirstName(),
						withdrawnPatient.getFirstName(), withdrawnPatient.getPatientId());

			}
		} else {
			if (withdrawnPatient.getAllowEmailNotification()) {
				sendEmailToParticipantAfterCRCWithdrawsPatient(withdrawnPatient.getCRC().getFirstName(),
						withdrawnPatient.getCRC().getLastName(), withdrawnPatient.getFirstName(),
						withdrawnPatient.getEmail(), questionAnswers.toString());
			}
			if (PortalAccountStatus.ACCT_ACTIVE.name()
					.equalsIgnoreCase(withdrawnPatient.getPortalAccountStatus().getCodeName())
					|| PortalAccountStatus.ACCT_INITIATED.name()
							.equalsIgnoreCase(withdrawnPatient.getPortalAccountStatus().getCodeName())) {
				String notificationTitle = notificationServiceConfig.getParticipantWithdrawnByCRCSubject()
						.concat(StringUtils.CR) + LocalDate.now();
				notificationService.addNotification(notificationServiceConfig.getParticipantWithdrawnByCRCFrom(),
						notificationTitle, notificationServiceConfig.getParticipantWithdrawnByCRCMessage(),
						withdrawnPatient.getUserId(), withdrawnPatient.getCRC().getFirstName(),
						withdrawnPatient.getCRC().getLastName(), withdrawnPatient.getPatientId());
			}
		}
		return Optional.of(userOptional.get());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> findByEmail(String emailId) {
		return userRepository.findByEmail(emailId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> updateUser(User user) {
		user.setLastRevisedDate(new Timestamp(System.currentTimeMillis()));
		return Optional.of(userRepository.save(user));
	}

	/**
	 * Method to send out email to CRC when an active participant withdraws self
	 * from the PPE Program
	 * 
	 * @param firstName       - Patient's First Name
	 * @param lastName        - Patient's Last Name
	 * @param salutationName  - Email Subscriber's first name
	 * @param emailId         - Email Subscriber's email id
	 * @param questionAnswers - Survey question and answers taken by the participant
	 * @return - Success or Failure of the email process
	 */
	private String sendEmailToCRCAfterParticipantWithdraws(String firstName, String lastName, String salutationName,
			String emailId, String questionAnswers) {

		/* Replace the variables in the EmailBody */
		String replaceStringWith[] = { firstName, lastName, salutationName, questionAnswers };
		String replaceThisString[] = { "%{FirstName}", "%{LastName}", "%{SalutationFirstName}", "%{questionAnswer}" };
		String updatedTextBody = StringUtils.replaceEach(
				emailServiceConfig.getEmailTextBodyForCRCWhenPatientWithdraws(), replaceThisString, replaceStringWith);

		/* Replace the variables in the Subject Line */
		String replaceSubjectStringWith[] = { firstName, lastName };
		String replaceThisStringForSubject[] = { "%{FirstName}", "%{LastName}" };
		String subject = StringUtils.replaceEach(emailServiceConfig.getEmailSubjectForCRCWhenPatientWithdraws(),
				replaceThisStringForSubject, replaceSubjectStringWith);

		return emailService.sendEmailNotification(emailId, emailServiceConfig.getSenderEmailAddress(), subject,
				updatedTextBody, updatedTextBody);
	}

	/**
	 * Method to send out email to Patient when a CRC withdraws that participant
	 * from the PPE Program
	 * 
	 * @param firstName       - CRC's first name
	 * @param lastName        - CRC's last name
	 * @param salutationName  - Email Subscriber's first name
	 * @param emailId         - Email Subscriber's email id
	 * @param questionAnswers - Survey question and answers taken by the CRC on
	 *                        behalf of the participant
	 * @return - Success or Failure of the email process
	 */
	private String sendEmailToParticipantAfterCRCWithdrawsPatient(String firstName, String lastName,
			String salutationName, String emailId, String questionAnswers) {

		/* Replace the variables in the EmailBody */
		String replaceStringWith[] = { firstName, lastName, salutationName, questionAnswers };
		String replaceThisString[] = { "%{FirstName}", "%{LastName}", "%{SalutationFirstName}", "%{questionAnswer}" };
		String updatedTextBody = StringUtils.replaceEach(
				emailServiceConfig.getEmailTextBodyForPatientWhenCRCWithdraws(), replaceThisString, replaceStringWith);

		return emailService.sendEmailNotification(emailId, emailServiceConfig.getSenderEmailAddress(),
				emailServiceConfig.getEmailSubjectForPatientWhenCRCWithdraws(), updatedTextBody, updatedTextBody);
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> invitePatientToPortal(String patientId, String uuid, String patientEmail,
			String patientFirstName, String patientLastName) throws JsonProcessingException {
		Optional<User> participantOptional = findActiveParticipantByPatientId(patientId);
		if (participantOptional.isEmpty()) {
			return participantOptional;
		}

		Participant participant = (Participant) participantOptional.get();
		if (StringUtils.isNotBlank(patientFirstName)) {
			participant.setFirstName(patientFirstName);
		}
		if (StringUtils.isNotBlank(patientLastName)) {
			participant.setLastName(patientLastName);
		}
		if (StringUtils.isNotBlank(patientEmail)) {
			participant.setEmail(patientEmail);
		}
		/* Get the UserId for CRC */
		Optional<User> crcOptional = findByUuid(uuid);
		if (crcOptional.isPresent()) {
			participant.setLastRevisedUser(crcOptional.get().getUserId());
			participant.setLastRevisedDate(new Timestamp(System.currentTimeMillis()));
		}

		Code portalAccountStatusCode = codeRepository.findByCodeName(PortalAccountStatus.ACCT_INITIATED.name());
		participant.setPortalAccountStatus(portalAccountStatusCode);

		participantOptional = Optional.of(userRepository.save(participant));

		raiseInvitedParticipationAuditEvent(patientId, uuid, patientEmail, patientFirstName, patientLastName);

		// Send Notification to Patient & Providers
		emailService.sendEmailToInvitePatient(patientEmail, patientFirstName);
		if (participant.getProviders() != null) {
			for (Provider provider : participant.getProviders()) {
				if (provider.getAllowEmailNotification() && StringUtils.isNotBlank(provider.getEmail())) {
					emailService.sendEmailToProviderOnPatientInvitation(provider.getEmail(), provider.getFirstName());
					String message = StringUtils.replace(
							notificationServiceConfig.getPatientReceivesInvitationMessage(), "%{FullName}",
							participant.getFullName());
					message = StringUtils.replace(message, "%{PatientID}", participant.getPatientId());
					notificationService.addNotification(notificationServiceConfig.getPatientReceivesInvitationFrom(),
							notificationServiceConfig.getPatientReceivesInvitationTitle().concat(StringUtils.CR)
									+ LocalDate.now(),
							message, provider.getUserId(), provider.getFirstName(), participant.getFullName(),
							patientId);
				}
			}
		}

		return participantOptional;
	}
	
	/*
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> insertNewPatientDetailsFromOpen(String patientId) {
		Participant newPatient = new Participant();
		newPatient.setPatientId(patientId);
		newPatient.setFirstName(StringUtils.EMPTY);
		newPatient.setLastName(StringUtils.EMPTY);
		Role role = roleRepository.findByRoleName(PPERole.ROLE_PARTICIPANT.getRoleName());
		newPatient.setRole(role);
		Code userType =  codeRepository.findByCodeName(UserType.PPE_PARTICIPANT.name());
		newPatient.setUserType(userType);
		Code portalAccountStatusCode = codeRepository.findByCodeName(PortalAccountStatus.ACCT_NEW.name());
		newPatient.setPortalAccountStatus(portalAccountStatusCode);	
		newPatient.setAllowEmailNotification(true);
		newPatient.setIsActiveBiobankParticipant(true);
		newPatient.setDateCreated(new Timestamp(System.currentTimeMillis()));
		newPatient.setLastRevisedDate(new Timestamp(System.currentTimeMillis()));
		
		return  Optional.of(userRepository.save(newPatient));
	}

	private void raiseInvitedParticipationAuditEvent(String patientId, String uuid, String patientEmail,
			String patientFirstName, String patientLastName) throws JsonProcessingException {
		ObjectNode auditDetail = mapper.createObjectNode();

		auditDetail.put("UUID", uuid).put("PatientID", patientId).put("PatientEmail", patientEmail)
				.put("PatientFirstName", patientFirstName).put("PatientLastName", patientLastName);
		String auditDetailString = mapper.writeValueAsString(auditDetail);
		auditService.logAuditEvent(auditDetailString, AuditEventType.PPE_INVITE_TO_PORTAL.name());
	}

}
