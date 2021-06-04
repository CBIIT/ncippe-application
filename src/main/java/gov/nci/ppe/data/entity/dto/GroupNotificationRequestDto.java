package gov.nci.ppe.data.entity.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import gov.nci.ppe.constants.ErrorConstants;
import gov.nci.ppe.constants.PPERole;
import gov.nci.ppe.data.entity.User;
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
@Data
public class GroupNotificationRequestDto {

	@NotNull(message = ErrorConstants.MISSING_REQUEST)
	private User requester;

	@NotEmpty(message = ErrorConstants.MISSING_NOTIFICATION_AUDIENCES)
	private List<PPERole> audiences;

	@NotNull(message = ErrorConstants.MISSING_SUBJECT)
	private SubjectDto subject;

	@NotNull(message = ErrorConstants.MISSING_MESSAGE)
	private MessageBody message;
}
