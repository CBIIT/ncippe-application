package gov.nci.ppe.data.entity;

import java.util.Optional;
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

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-01
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@Table(name = "User")
@DiscriminatorValue("4")
public class CRC extends User {
	

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "CRCParticipant", joinColumns = @JoinColumn(name = "CRCId"), inverseJoinColumns = @JoinColumn(name = "ParticipantId"))
	private Set<Participant> patients;	
	
	@EqualsAndHashCode.Include
	@Column(name = "OpenCtepID", nullable = false, length = 32)
	private Long openCtepID;


	@Override
	public String toString() {
		StringBuilder retValue = new StringBuilder("{") ;
		retValue.append(StringUtils.CR).append(" Name : ").append(this.getFirstName()).append(" ")
				.append(this.getLastName())
				.append(StringUtils.CR).append("Phone : ").append(this.getPhoneNumber()).append(",")
				.append(StringUtils.CR).append("Email Id : ").append(this.getEmail())
				.append(StringUtils.CR).append("}");
		return retValue.toString();
	}

	/**
	 * Method to check if the CRC has an assigned patient with the given patient ID
	 * 
	 * @param patientId - PatientID of the participant
	 * 
	 * @return Optional object with Participant object if found, or empty otherwise
	 */
	public Optional<Participant> hasPatient(String patientId) {
		return getPatients().stream().filter(provider -> patientId.equals(provider.getUserUUID())).findAny();
	}
}
