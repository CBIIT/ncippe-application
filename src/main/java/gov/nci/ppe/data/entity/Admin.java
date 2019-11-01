package gov.nci.ppe.data.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-01
 */
@Entity
@DiscriminatorValue("3")
public class Admin extends User {

	@Column(name="UserId")
	private Long adminId;

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}
	
}
