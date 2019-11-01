package gov.nci.ppe.data.repository;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-07-22
 */
import org.springframework.data.jpa.repository.JpaRepository;

import gov.nci.ppe.data.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
