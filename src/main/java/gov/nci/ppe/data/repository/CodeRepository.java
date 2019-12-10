package gov.nci.ppe.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import gov.nci.ppe.data.entity.Code;

/**
 * This is a configuration class that fetches all the required values from  EmailService.properties in the classpath.
 * @author PublicisSapient
 * @version 1.0 
 * @since   2019-08-15
 */
public interface CodeRepository extends JpaRepository<Code, Long> {
	Code findByCodeName(@Param("codeName") String codeName);
	
	List<Code> findByCodeNameIn(List<String> codeNameList);
}
