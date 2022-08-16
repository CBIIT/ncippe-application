package gov.nci.ppe.services;

import org.springframework.stereotype.Service;

import gov.nci.ppe.data.entity.User;

@Service
public interface AuthorizationService {

	/**
	 * Verify that the requester has the authority to access the requested User
	 * information
	 * 
	 * @param requestingUserUUID - Requesting User's UUID
	 * @param targetUser         - User for whom authorization is requested
	 * 
	 * @return true if authorized, false otherwise
	 */
	public boolean authorize(String requestingUserUUID, User targetUser);

	/**
	 * Verify that the requester has the authority to access the requested User
	 * information
	 * 
	 * @param requestingUserUUID - Requesting User's UUID
	 * @param targetUUID         - UUID of User for which authorization is requested
	 * 
	 * @return true if authorized, false otherwise
	 */
	public boolean authorize(String requestingUserUUID, String targetUUID);

	/**
	 * Verify the requester has the authority to upload the file type for the
	 * specified user
	 * 
	 * @param requestingUserUUID - Requesting User's UUID
	 * @param targetPatientId    - Patient ID of User for whom authorization is
	 *                           requested
	 * @param fileType           - type of file, currently
	 *                           PPE_FILETYPE_BIOMARKER_REPORT or
	 *                           PPE_FILETYPE_ECONSENT_FORM
	 * @return true if authorized, false otherwise
	 */
	public boolean authorizeFileUpload(String requestingUserUUID, String targetPatientId, String fileType);

	/**
	 * Verify the requester has the authority to download the file type for the
	 * specified user
	 * 
	 * @param requestingUserUUID - Requesting User's UUID
	 * @param targetPatientId    - Patient ID of User for whom authorization is
	 *                           requested
	 * @param fileType           - type of file, currently
	 *                           PPE_FILETYPE_BIOMARKER_REPORT or
	 *                           PPE_FILETYPE_ECONSENT_FORM
	 * @return true if authorized, false otherwise
	 */
	public boolean authorizeFileDownload(String requestingUserUUID, String targetPatientId, String fileType);

}
