package gov.nci.ppe.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.nci.ppe.data.entity.GroupNotificationRequest;

/**
 * Repository interface for {@link GroupNotificationRequest}
 * 
 * @author PublicisSapient
 * 
 * @version 2.3
 *
 * @since Apr 30, 2021
 *
 */
@Repository
public interface GroupNotificationRequestRepository extends JpaRepository<GroupNotificationRequest, Long> {

}
