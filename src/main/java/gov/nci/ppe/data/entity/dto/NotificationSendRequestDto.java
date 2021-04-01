package gov.nci.ppe.data.entity.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import gov.nci.ppe.constants.ErrorConstants;
import gov.nci.ppe.constants.PPEUserType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * DTO for a request to send messages
 * 
 * @author PublicisSapient
 * 
 * @version 2.0
 *
 * @since Mar 24, 2021
 *
 */

@ApiModel(value = "Details of Message to be sent out")
@Data
public class NotificationSendRequestDto {

	@NotEmpty(message = ErrorConstants.MISSING_NOTIFICATION_AUDIENCES)
	@ApiModelProperty(value = "List of roles")
	private List<PPEUserType> audiences;

	@NotNull(message = ErrorConstants.MISSING_SUBJECT)
	@ApiModelProperty(value = "Subject of Message")
	private Subject subject;

	@NotNull(message = ErrorConstants.MISSING_MESSAGE)
	@ApiModelProperty(value = "Body of the Message")
	private Message message;

	@Data
	private class Message {
		@NotBlank(message = ErrorConstants.MISSING_ENGLISH_MESSAGE)
		@ApiModelProperty(value = "Message Body in english")
		private String en;

		@NotBlank(message = ErrorConstants.MISSING_SPANISH_MESSAGE)
		@ApiModelProperty(value = "Message Body in Spanish")
		private String es;
	}

	@Data
	private class Subject {
		@NotBlank(message = ErrorConstants.MISSING_ENGLISH_SUBJECT)
		@ApiModelProperty(value = "Message Subject in english")
		private String en;

		@NotBlank(message = ErrorConstants.MISSING_SPANISH_SUBJECT)
		@ApiModelProperty(value = "Message Subject in Spanish")
		private String es;
	}
}
