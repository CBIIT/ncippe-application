package gov.nci.ppe.data.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-01
 */
@Entity
@Table(name = "User")
@DiscriminatorValue("4")
public class CRC extends User {
	
	@Column(name = "UserId")
	
	private Long crcId;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "CRCParticipant", joinColumns = @JoinColumn(name = "CRCId"), inverseJoinColumns = @JoinColumn(name = "ParticipantId"))
	private Set<Participant> patients;	
	
	@Column(name = "OpenCtepID", nullable = false, length = 32)
	private Long openCtepID;

	public Long getCrcId() {
		return crcId;
	}

	public void setCrcId(Long crcId) {
		this.crcId = crcId;
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

	public String toString() {
		StringBuilder retValue = new StringBuilder("{") ;
		retValue.append(StringUtils.CR).append(" CRC Id : ").append(crcId).append(",")
				.append(StringUtils.CR).append(" Name : ").append(this.getFirstName()).append(" ").append(this.getLastName())
				.append(StringUtils.CR).append("Phone : ").append(this.getPhoneNumber()).append(",")
				.append(StringUtils.CR).append("Email Id : ").append(this.getEmail())
				.append(StringUtils.CR).append("}");
		return retValue.toString();
	}
}
