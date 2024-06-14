package gov.nci.ppe.data.entity.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProjectSummaryDTO
{
	//private Long id;

	@JsonProperty("participantsCount")
	private Long participantsCount;
	
	@JsonProperty("sitesCount")
	private Long sitesCount;
	
	@JsonProperty("cancerTypesCount")
	private Long cancerTypesCount;
	
	@JsonProperty("bioMarkerReturnedCount")
	private Long bioMarkerReturnedCount;
	
	@JsonProperty("lastRevisedDate")
	private Timestamp lastRevisedDate;

}
