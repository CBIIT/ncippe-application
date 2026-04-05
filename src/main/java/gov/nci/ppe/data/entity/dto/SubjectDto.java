package gov.nci.ppe.data.entity.dto;

import javax.validation.constraints.NotBlank;

import gov.nci.ppe.constants.ErrorConstants;
import io.swagger.v3.oas.annotations.media.Schema;
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
	@Schema(description = "Message Subject in english")
	private String en;

	@NotBlank(message = ErrorConstants.MISSING_SPANISH_SUBJECT)
	@Schema(description = "Message Subject in Spanish")
	private String es;
}