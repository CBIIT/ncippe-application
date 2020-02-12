package gov.nci.ppe.services;

import java.util.Optional;

import org.springframework.stereotype.Component;

import gov.nci.ppe.data.entity.User;

@Component
public interface AuthorizationService {

	/**
	 * Verify that the requester has the authority to access the requested target
	 * 
	 * @param authToken  - JWT Claim with authentication details
	 * @param targetUser - User for which authorization is requested
	 * 
	 * @return true if authorized, false otherwise
	 */
	public boolean authorize(String authToken, User targetUser);

	/**
	 * Verify that the requester has the authority to access the requested target
	 * 
	 * @param authToken  - JWT Claim with authentication details
	 * @param targetUUID - UUID of User for which authorization is requested
	 * 
	 * @return true if authorized, false otherwise
	 */
	public boolean authorize(String authToken, String targetUUID);

	/**
	 * Fetches the User object matching the details in the JWT claim
	 * 
	 * @param authToken - JWT Claim with authentication details
	 * 
	 * @return the authenticated User
	 */
	public Optional<User> getRequestingUser(String authToken);
}
