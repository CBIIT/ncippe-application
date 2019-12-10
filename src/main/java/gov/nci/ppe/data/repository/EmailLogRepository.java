package gov.nci.ppe.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.nci.ppe.data.entity.EmailLog;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-13
 */
@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {

}
