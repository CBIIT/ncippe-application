package gov.nci.ppe.data.entity.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonView;

public class CrcDTO extends UserDTO{
	
	private Long crcId;
	
	@JsonView(JsonViews.CrcDetailView.class)
	private Set<ParticipantDTO> patients;

	public Long getCrcId() {
		return crcId;
	}

	public void setCrcId(Long crcId) {
		this.crcId = crcId;
	}

	public Set<ParticipantDTO> getPatients() {
		return patients;
	}

	public void setPatients(Set<ParticipantDTO> patients) {
		this.patients = patients;
	}

}
