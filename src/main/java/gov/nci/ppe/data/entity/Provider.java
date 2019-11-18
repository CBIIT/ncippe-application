package gov.nci.ppe.data.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-07-29
 */
@Entity
@DiscriminatorValue("2")
public class Provider extends User {
	
	@Column(name="UserId")
	private Long providerId;
	
	@ManyToMany(mappedBy = "providers")
	private Set<Participant> patients = new HashSet<>();
	
	@Column(name = "OpenCtepID", nullable = false, length = 32)
	private Long openCtepID;
	
	public Long getProviderId() {
		return providerId;
	}

	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}

	public Set<Participant> getPatients() {
		return patients;
	}

	public void setPatients(Set<Participant> patients) {
		this.patients = patients;
	}

	/**
	 * @return the openCtepID
	 */
	public Long getOpenCtepID() {
		return openCtepID;
	}

	/**
	 * @param openCtepID the openCtepID to set
	 */
	public void setOpenCtepID(Long openCtepID) {
		this.openCtepID = openCtepID;
	}
}
