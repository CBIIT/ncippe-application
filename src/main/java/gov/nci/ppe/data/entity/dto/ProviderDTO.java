package gov.nci.ppe.data.entity.dto;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonView;

public class ProviderDTO extends UserDTO {

	@JsonView(JsonViews.ProviderDetailView.class)
	private Set<ParticipantDTO> patients = new HashSet<>();

	@JsonView(JsonViews.ProviderDetailView.class)
	private CrcDTO crcDto;
	

	public CrcDTO getCrcDto() {
		return crcDto;
	}

	public void setCrcDto(CrcDTO crcDto) {
		this.crcDto = crcDto;
	}

	public Set<ParticipantDTO> getPatients() {
		return patients;
	}

	public void setPatients(Set<ParticipantDTO> patients) {
		this.patients = patients;
	}
}
