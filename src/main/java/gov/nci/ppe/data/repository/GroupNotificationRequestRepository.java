package gov.nci.ppe.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.nci.ppe.data.entity.GroupNotificationRequest;

@Repository
public interface GroupNotificationRequestRepository extends JpaRepository<GroupNotificationRequest, Long> {

}
