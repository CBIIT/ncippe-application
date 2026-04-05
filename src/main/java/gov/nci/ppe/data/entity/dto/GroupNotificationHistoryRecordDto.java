package gov.nci.ppe.data.entity.dto;

import java.sql.Timestamp;
import java.util.List;

import gov.nci.ppe.constants.PPERole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO representing a Group Notification Request record
 * 
 * @author PublicisSapient
 * 
 * @version 2.3
 *
 * @since Apr 30, 2021
 *
 */
@Data
@Schema(description = "Record of previous request to send Group Notification")
public class GroupNotificationHistoryRecordDto {
	@Schema(description = "List of roles the message was sent to")
	private List<PPERole> audiences;

	@Schema(description = "Subject of Message")
	private SubjectDto subject;

	@Schema(description = "Body of the Message")
	private MessageBody message;

	@Schema(description = "Request sent on")
	private Timestamp dateSent;

	private NotificationSenderDto messageFrom;
}
