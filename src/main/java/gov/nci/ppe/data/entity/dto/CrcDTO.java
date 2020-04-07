package gov.nci.ppe.data.entity.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class CrcDTO extends UserDTO{
	
	@EqualsAndHashCode.Include
	private Long openCtepID;

	@JsonView(JsonViews.CrcDetailView.class)
	private Set<ParticipantDTO> patients;

}
