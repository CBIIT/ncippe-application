package gov.nci.ppe.data.entity.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class ProjectSummaryDTO
{
	//private Long id;

	private Long participantsCount;

	private Long sitesCount;

	private Long cancerTypesCount;
	
	private Long biomarkerReturnedCount;

	private Timestamp lastRevisedDate;

}
