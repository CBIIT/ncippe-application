package gov.nci.ppe.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.nci.ppe.data.entity.Code;
import gov.nci.ppe.data.entity.User;

/**
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-07-22
 */

public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Returns an User entity matching the supplied UserUUID
	 * 
	 * @param userUUID - Unique ID of the User to search for
	 * @return User object found.
	 */
	Optional<User> findByUserUUID(String userUUID);

	/**
	 * Returns an User entity matching the input Email
	 * 
	 * @param email - email id of the User
	 * @return User object found
	 */
	Optional<User> findByEmail(String email);

	/**
	 * Returns an User entity matching the supplied UserGUID and PortalAccountStatus
	 * 
	 * @param userUUID          - Unique ID of the User to search for
	 * @param accountStatusList - List of possible Account Status
	 * @return User object found.
	 */
	Optional<User> findByUserUUIDAndPortalAccountStatusIn(String userUUID, List<Code> accountStatusList);

	/**
	 * Returns an User entity matching the supplied Email and PortalAccountStatus
	 * 
	 * @param email             - Email of the User to search for
	 * @param accountStatusList - List of possible Account Status
	 * @return User object found.
	 */
	Optional<User> findByEmailAndPortalAccountStatusIn(String email, List<Code> accountStatusCodeList);

	/**
	 * Returns the list of {@link User} entities matching the specified User Type
	 * @param userTypeList - List of user types to fetch users for
	 * @return the list of Users
	 */
	List<User> findByUserTypeIn(List<Code> userTypeList);
}
