package gov.nci.ppe.data.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-07-29
 */
@Entity
@DiscriminatorValue("2")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Provider extends User {
	
	
	@ManyToMany(mappedBy = "providers")
	private Set<Participant> patients = new HashSet<>();
	
	@EqualsAndHashCode.Include
	@Column(name = "OpenCtepID", nullable = false, length = 32)
	private Long openCtepID;
	

}
