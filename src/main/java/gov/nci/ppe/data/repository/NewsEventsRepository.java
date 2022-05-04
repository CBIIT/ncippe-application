package gov.nci.ppe.data.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.nci.ppe.data.entity.NewsEvent;

/**
 * JPA Interface for {@Link NewsEvent} Repository
 * 
 * @author PublicisSapient
 *
 * @version 2.6
 *
 * @since Mar 24, 2022
 */
public interface NewsEventsRepository extends JpaRepository<NewsEvent, Long> {

	List<NewsEvent> findByExpirationDateAfter(LocalDateTime valueOf);

}
