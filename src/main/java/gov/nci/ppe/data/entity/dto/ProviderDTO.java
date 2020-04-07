package gov.nci.ppe.data.entity.dto;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class ProviderDTO extends UserDTO {

	@JsonView(JsonViews.ProviderDetailView.class)
	private Set<ParticipantDTO> patients = new HashSet<>();

	@JsonView(JsonViews.ProviderDetailView.class)
	private CrcDTO crcDto;

	@EqualsAndHashCode.Include
	private Long openCtepID;
}
