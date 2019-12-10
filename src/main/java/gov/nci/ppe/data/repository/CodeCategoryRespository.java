package gov.nci.ppe.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.nci.ppe.data.entity.CodeCategory;

public interface CodeCategoryRespository extends JpaRepository<CodeCategory, Long> {

}
