package gov.nci.ppe.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.nci.ppe.data.entity.GroupNotificationRequest;
import gov.nci.ppe.data.entity.User;

/**
 * Repository interface for {@link GroupNotificationRequest}
 * 
 * @author PublicisSapient
 * 
 * @version 2.4
 *
 * @since Apr 30, 2021
 *
 */
@Repository
public interface GroupNotificationRequestRepository extends JpaRepository<GroupNotificationRequest, Long> {

	/**
	 * Find All {@link GroupNotificationRequest} objects associated with the
	 * specified {@link User}
	 * 
	 * @param requester - the User whose request history is to be retrieved
	 * @return the List of {@link GroupNotificationRequest} objects found
	 */
	List<GroupNotificationRequest> findByRequester(User requester);

}
