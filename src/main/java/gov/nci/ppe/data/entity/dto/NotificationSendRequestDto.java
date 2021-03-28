package gov.nci.ppe.data.entity.dto;

import java.util.List;

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

	@ApiModelProperty(value = "List of roles")
	private List<PPEUserType> audiences;

	@ApiModelProperty(value = "Subject of Message")
	private Subject subject;

	@ApiModelProperty(value = "Body of the Message")
	private Message message;

	@Data
	private class Message {
		@ApiModelProperty(value = "Message Body in english")
		private String en;

		@ApiModelProperty(value = "Message Body in Spanish")
		private String es;
	}

	@Data
	private class Subject {
		@ApiModelProperty(value = "Message Subject in english")
		private String en;

		@ApiModelProperty(value = "Message Subject in Spanish")
		private String es;
	}
}
