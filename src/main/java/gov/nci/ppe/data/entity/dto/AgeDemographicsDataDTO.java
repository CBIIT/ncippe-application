package gov.nci.ppe.data.entity.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AgeDemographicsDataDTO
{
	//private Long id;
	
	@JsonProperty("label")
	private String ageGroup;

	@JsonProperty("value")
	private Long count;

}
