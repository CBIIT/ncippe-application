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
@DiscriminatorValue("6")
public class Mocha extends User {

	@Column(name="UserId")
	private Long mochaAdminId;

	public Long getBsscId() {
		return mochaAdminId;
	}

	public void setBsscId(Long bsscId) {
		this.mochaAdminId = bsscId;
	}

}
