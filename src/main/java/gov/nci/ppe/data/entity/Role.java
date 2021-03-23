package gov.nci.ppe.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-07-22
 */
@Data
@Entity
public class Role {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roleId;
	
	@Column(name="RoleName", nullable = false, length = 32)
	private String roleName;
	
	@Column(name="Description", nullable = true, length = 255)
	private String description;


}
