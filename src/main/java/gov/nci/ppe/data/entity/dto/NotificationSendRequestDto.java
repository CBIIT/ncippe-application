package gov.nci.ppe.data.entity.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import gov.nci.ppe.constants.ErrorConstants;
import gov.nci.ppe.constants.PPEUserType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * DTO for a request to send Group Notifications
 * 
 * @author PublicisSapient
 * 
 * @version 2.3
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
	private MessageBody message;
}
