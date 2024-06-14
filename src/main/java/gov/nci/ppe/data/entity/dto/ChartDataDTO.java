package gov.nci.ppe.data.entity.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ChartDataDTO
{
	//private Long id;

	//private String chart;

	@JsonProperty("name")
	private String dataType;

	@JsonProperty("value")
	private Long dataValue;

	@JsonProperty("label")
	private String dataLabel;

}
