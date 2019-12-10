package gov.nci.ppe.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.nci.ppe.data.entity.PortalNotification;

@Repository
public interface PortalNotificationRepository extends JpaRepository<PortalNotification, Long>{
	
	@Query(value = "SELECT * FROM PortalNotification PN WHERE PN.UserId = :userId",nativeQuery=true)
	List<PortalNotification> findNotificationstByUserId(@Param("userId") Long userId);

}
