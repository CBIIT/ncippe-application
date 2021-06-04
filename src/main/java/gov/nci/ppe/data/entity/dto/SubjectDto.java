package gov.nci.ppe.data.entity.dto;

import javax.validation.constraints.NotBlank;

import gov.nci.ppe.constants.ErrorConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * DTO representing the Subject of a notifcation including SPanish and English
 * versions
 * 
 * @author PublicisSapient
 * 
 * @version 2.3
 *
 * @since Apr 30, 2021
 *
 */
@Data
public class SubjectDto {
	@NotBlank(message = ErrorConstants.MISSING_ENGLISH_SUBJECT)
	@ApiModelProperty(value = "Message Subject in english")
	private String en;

	@NotBlank(message = ErrorConstants.MISSING_SPANISH_SUBJECT)
	@ApiModelProperty(value = "Message Subject in Spanish")
	private String es;
}