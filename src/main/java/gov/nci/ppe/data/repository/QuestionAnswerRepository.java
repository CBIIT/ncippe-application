package gov.nci.ppe.data.repository;
/**
 * JPA class representing the table QuestionAnswer
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-09-29
 */
import org.springframework.data.jpa.repository.JpaRepository;

import gov.nci.ppe.data.entity.QuestionAnswer;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {

}
