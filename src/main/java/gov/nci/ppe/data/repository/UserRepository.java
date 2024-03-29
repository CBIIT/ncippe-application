package gov.nci.ppe.data.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.nci.ppe.data.entity.Code;
import gov.nci.ppe.data.entity.Role;
import gov.nci.ppe.data.entity.User;

/**
 * Repository Interface for methods related to {@link User}
 * 
 * @author PublicisSapient
 * @version 2.3
 * @since 2019-07-22
 */
@Repository
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
	 * Returns an {@link User} entity matching the supplied Email and
	 * PortalAccountStatus
	 * 
	 * @param email             - Email of the User to search for
	 * @param accountStatusList - List of possible Account Status
	 * @return User object found.
	 */
	Optional<User> findByEmailAndPortalAccountStatusIn(String email, List<Code> accountStatusCodeList);

	/**
	 * Returns the list of {@link User} entities matching the specified User Type
	 * 
	 * @param userTypeList - List of user types to fetch users for
	 * @return the list of Users
	 */
	List<User> findByUserTypeIn(List<Code> userTypeList);

	/**
	 * Returns the list of {@link User} entities with the specified Role
	 * 
	 * @param roles - list of {@link Role} to fetch users for
	 * @return the list of Users
	 */
	List<User> findByRoleIn(Set<Role> roles);
}
