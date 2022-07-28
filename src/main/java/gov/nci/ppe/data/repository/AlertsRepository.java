package gov.nci.ppe.data.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.nci.ppe.data.entity.Alert;

@Repository
public interface AlertsRepository extends JpaRepository<Alert, Integer> {

	List<Alert> findByExpirationDateGreaterThanOrExpirationDateIsNull(LocalDateTime dateExpired);
}
