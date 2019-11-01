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
@DiscriminatorValue("5")
public class BSSC extends User {

	@Column(name="UserId")
	private Long bsscId;

	public Long getBsscId() {
		return bsscId;
	}

	public void setBsscId(Long bsscId) {
		this.bsscId = bsscId;
	}

}
