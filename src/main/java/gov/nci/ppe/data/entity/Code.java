package gov.nci.ppe.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-07-22
 */
@Entity
@Table(name = "Code")
public class Code {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codeId;

	@Column(name="CodeName", nullable = false, length = 32)
	private String codeName;
	
	@Column(name="Description", nullable = true, length = 255)
	private String description;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="CodeCategoryId")
	@Fetch(FetchMode.JOIN)
	private CodeCategory codeCategory;
	
	@Column(name="IsActive", nullable = false)
	private int isActive;
	
	public Long getCodeId() {
		return codeId;
	}

	public void setCodeId(Long codeId) {
		this.codeId = codeId;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public CodeCategory getCodeCategory() {
		return codeCategory;
	}

	public void setCodeCategory(CodeCategory codeCategory) {
		this.codeCategory = codeCategory;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	
}
