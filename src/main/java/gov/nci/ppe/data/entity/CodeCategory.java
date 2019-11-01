package gov.nci.ppe.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-07-22
 */
@Entity
@Table(name = "CodeCategory")
public class CodeCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codeCategoryId;
	
	@Column(name="Name", nullable = false, length = 32)
	private String name;
	
	@Column(name="Description", nullable = true, length = 255)
	private String description;

	public Long getCodeCategoryId() {
		return codeCategoryId;
	}

	public void setCodeCategoryId(Long codeCategoryId) {
		this.codeCategoryId = codeCategoryId;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
