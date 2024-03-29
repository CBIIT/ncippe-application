package gov.nci.ppe.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.nci.ppe.constants.CommonConstants.LanguageOption;
import gov.nci.ppe.constants.PPERole;
import gov.nci.ppe.data.entity.CRC;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.Provider;
import gov.nci.ppe.data.entity.QuestionAnswer;
import gov.nci.ppe.data.entity.Role;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.exception.BusinessConstraintViolationException;
import gov.nci.ppe.open.data.entity.dto.OpenResponseDTO;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-07-22
 */
public interface UserService {

	/**
	 * This method fetches all the registered users and converts the User Object
	 * into UserDTO object
	 * 
	 * @return a String representing JSON Object
	 */
	public List<User> getAllRegisteredUsers();

	/**
	 * Fetch the User details based on GUID which is unique
	 * 
	 * @param userGUID - GUID of the user
	 * @return JSON format string containing user information.
	 */
	public Optional<User> findByUuid(String userGuid);

	/**
	 * This method will update the registered user's email notification preference
	 * and phone number.
	 * 
	 * @param phoneNumber            - the value can be empty string if the user
	 *                               wishes to delete his/her number.
	 * @param preferredLang          - Preferred Language for the User
	 * @param userGUID               - GUID of the user
	 * @param allowEmailNotification - value can be only 1 (true) or 2 (false)
	 * @param requestingUserUUID     - requesting User UUID
	 * 
	 * @return JSON String containing User information that was updated
	 */
	public Optional<User> updateUserDetails(String userGuid, Boolean userEmailNotification, String phoneNumber,
			LanguageOption preferredLang, String requestingUserUUID);

	/**
	 * Fetch the user based on the user's UserId
	 * 
	 * @param userId - UserId of the user.
	 * @return a User Object
	 */
	public Optional<User> findUserById(Long userId);

	/**
	 * Activates an User record by filling in the UUID value from login.gov.
	 * 
	 * @param userEmail - email of the user to activate
	 * @param userUUID  - UUID (from login.gov)
	 * @return - the updated User
	 */
	public Optional<User> activateUser(String userEmail, String userUUID);

	/**
	 * Fetch the user based on their userGUID and PortalAccountStatus.
	 * 
	 * @param userGUID          - GUID of the user
	 * @param accountStatusList - List of potential Portal Account Status.
	 * @return a User Object
	 */
	public Optional<User> findByUuidAndPortalAccountStatus(String userGuid, List<String> accountStatusList);

	/**
	 * Update the portalAccountStatus of current user. The user can move from
	 * INITIATE to ACTIVE to INACTIVE.
	 * 
	 * @param userId                  - UserId of the user.
	 * @param portalAccountStatusCode - Code object containing the status to be
	 *                                updated to.
	 * @return a User Object whose status will be deactiviated
	 */
	public Optional<User> deactivateUserPortalAccountStatus(String userGuid);

	/**
	 * Withdraw patient participation from Biobank program.
	 * 
	 * @param patient   - Participant object representing the patient who is been
	 *                  withdrawn from PPE
	 * @param qsAnsList - A list of survey questions and their answers provided
	 *                  during withdrawal
	 * @return - a User Object whose status will be withdrawn from the program
	 */
	public Optional<User> withdrawParticipationFromBiobankProgram(Participant patient, List<QuestionAnswer> qsAnsList);

	/**
	 * Fetch the user based on their email and PortalAccountStatus.
	 * 
	 * @param email             - email of the user
	 * @param accountStatusList - List of potential Portal Account Status.
	 * @return a User Object
	 */
	public Optional<User> findByEmailAndPortalAccountStatus(String email, List<String> validAccountStatusList);

	/**
	 * Fetch the Participant based on their Patient ID and PortalAccountStatus.
	 * 
	 * @param patientId         - GUID of the user
	 * @param accountStatusList - List of potential Portal Account Status.
	 * @return a User Object
	 */
	public Optional<User> findByPatientIdAndPortalAccountStatus(String patientId, List<String> validAccountStatusList);

	/**
	 * Fetch a participant who is active in the bio bank program by their patient id
	 * 
	 * @param patientId - Patient number
	 * @return a User object
	 */
	public Optional<User> findActiveParticipantByPatientId(String patientId);

	/**
	 * Withdraw patient participation from Biobank program and sends an email to
	 * subscribers.
	 * 
	 * @param patient   - Participant object representing the patient who is been
	 *                  withdrawn from PPE
	 * @param qsAnsList - A list of survey questions and their answers provided
	 *                  during withdrawal
	 * @return - a User Object whose status will be withdrawn from the program
	 */
	public Optional<User> withdrawParticipationFromBiobankProgramAndSendNotification(Participant patient,
			List<QuestionAnswer> qsAnsList);

	/**
	 * Fetch a user by their email id
	 * 
	 * @param emailId - Email id to search for the user
	 * @return a User Object
	 */
	public Optional<User> findByEmail(String emailId);

	/**
	 * Update the User record in database
	 * 
	 * @param user - a User Object with updated values
	 * @return a User Object
	 */
	public Optional<User> updateUser(User user);

	/**
	 * Updates a Patient record to include patient name and email, changes state
	 * from NEW to INITIATED, and sends an invitation email to the patient.
	 * 
	 * @param patientId - Unique Patient Id of the Patient
	 * @param uuid      - Unique login Id of the CRC performing the action
	 * @return Updated Patient record
	 * @throws JsonProcessingException
	 */
	public Optional<User> invitePatientToPortal(String patientId, String uuid) throws JsonProcessingException;

	/**
	 * Inserts a new patient record into PPE database based on the patient ID if one
	 * doesn't already exist. The patient details are fetched from OPEN.
	 * 
	 * @param patientId - Unique patientId that OPEN sends.
	 * @return - User object
	 */
	public Optional<User> insertNewPatientDetailsFromOpen(Participant newPatient);

	/**
	 * Inserts a new provider record into PPE database
	 * 
	 * @param provider - An entity object contain mandatory details.
	 * @return Optional User object
	 */
	public Optional<User> insertNewProviderDetailsFromOpen(Provider provider);

	/**
	 * Find a provider based on the CtepId passed. CtepID is provided by OPEN.
	 * 
	 * @param ctepId
	 * @return an optional User.
	 */
	public Optional<Provider> findProviderByCtepId(Long ctepId);

	/**
	 * Inserts a new CRC record into PPE database
	 * 
	 * @param provider - An entity object contain mandatory details.
	 * @return Optional User object
	 */
	public Optional<User> insertNewCRCDetailsFromOpen(CRC crc);

	/**
	 * Find a CRC based on the CtepId passed. CtepID is provided by OPEN.
	 * 
	 * @param ctepId
	 * @return an optional User.
	 */
	public Optional<CRC> findCRCByCtepId(Long ctepId);

	/**
	 * Insert the data fetched from OPEN into PPE DB
	 * 
	 * @param openResponseDTO - Data from OPEN
	 * @return List of Users that were inserted into PPE DB
	 */
	public List<User> insertDataFetchedFromOpen(OpenResponseDTO openResponseDTO);

	/**
	 * Update an existing patient with details fetched from OPEN
	 * 
	 * @param existingPatient - Existing Patient in PPE
	 * @return an optional User.
	 */
	public Optional<User> updatePatientDetailsFromOpen(Participant existingPatient);

	/**
	 * Generate Email and System Notification to Users who have unread reports for
	 * the specified number of days
	 * 
	 * @param daysUnread - number of days report must be unread to trigger
	 *                   notification
	 */
	public void generateUnreadReportReminderNotification(int daysUnread);

	/**
	 * Get all active users with the specified {@link PPERole}
	 * 
	 * @param set - List of {@link PPERole}
	 * @return the list of {@link User} records
	 */
	public List<User> getUsersByRole(Set<Role> set);

	/**
	 * Update Email address of {@link Participant} with the specified Patient ID
	 * only if the participant has not already logged in.
	 * 
	 * @param patientId - Patient Id of the Participant to modify
	 * @param newEmail  - New Email address of the participant
	 * @param crcUuid   - UniqueId of CRC requesting the change
	 * @return the updated {@link Participant} record.
	 * @throws BusinessConstraintViolationException - If the participant has already
	 *                                              logged in
	 */
	public Optional<User> updatePatientEmail(String patientId, String newEmail, String crcUuid)
			throws BusinessConstraintViolationException;

	/**
	 * Checks that the UUID and Email provided by login.gov matches the the User
	 * record and updates if needed.
	 * 
	 * @param user     - the User record to update
	 * @param uuid     - Unique Identifier received from login.gov
	 * @param newEmail - email received from login.gov
	 * @return - the updated User record.
	 */
	public Optional<User> synchronizeUserEmailWithLogin(User user, String uuid, String newEmail);
}
