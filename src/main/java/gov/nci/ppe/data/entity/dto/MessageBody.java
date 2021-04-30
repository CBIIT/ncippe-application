package gov.nci.ppe.data.entity.dto;

import javax.validation.constraints.NotBlank;

import gov.nci.ppe.constants.ErrorConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * DTO representing the message body containing both Spanish and English text
 * 
 * @author PublicisSapient
 * 
 * @version 2.3
 *
 * @since Apr 30, 2021
 *
 */
@Data
@ApiModel(value = "Message Body structure")
public class MessageBody {
	@NotBlank(message = ErrorConstants.MISSING_ENGLISH_MESSAGE)
	@ApiModelProperty(value = "Message Body in english")
	private String en;

	@NotBlank(message = ErrorConstants.MISSING_SPANISH_MESSAGE)
	@ApiModelProperty(value = "Message Body in Spanish")
	private String es;
}