package gov.nci.ppe.data.entity.dto;

import javax.validation.constraints.NotBlank;

import gov.nci.ppe.constants.ErrorConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Message {
	@NotBlank(message = ErrorConstants.MISSING_ENGLISH_MESSAGE)
	@ApiModelProperty(value = "Message Body in english")
	private String en;

	@NotBlank(message = ErrorConstants.MISSING_SPANISH_MESSAGE)
	@ApiModelProperty(value = "Message Body in Spanish")
	private String es;
}