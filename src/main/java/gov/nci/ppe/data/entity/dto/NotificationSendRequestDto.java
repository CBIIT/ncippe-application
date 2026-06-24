package gov.nci.ppe.data.entity.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import gov.nci.ppe.constants.ErrorConstants;
import gov.nci.ppe.constants.PPEUserType;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(description = "Details of Message to be sent out")
@Data
public class NotificationSendRequestDto {

	@NotEmpty(message = ErrorConstants.MISSING_NOTIFICATION_AUDIENCES)
	@Schema(description = "List of roles")
	private List<PPEUserType> audiences;

	@NotNull(message = ErrorConstants.MISSING_SUBJECT)
	@Schema(description = "Subject of Message")
	private SubjectDto subject;

	@NotNull(message = ErrorConstants.MISSING_MESSAGE)
	@Schema(description = "Body of the Message")
	private MessageBody message;
}
