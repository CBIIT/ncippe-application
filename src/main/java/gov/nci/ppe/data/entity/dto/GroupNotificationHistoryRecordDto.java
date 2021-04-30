package gov.nci.ppe.data.entity.dto;

import java.sql.Timestamp;
import java.util.List;

import gov.nci.ppe.constants.PPEUserType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "Record of previous request to send Group Notification")
public class GroupNotificationHistoryRecordDto {
	@ApiModelProperty(value = "List of roles the message was sent to")
	private List<PPEUserType> audiences;

	@ApiModelProperty(value = "Subject of Message")
	private Subject subject;

	@ApiModelProperty(value = "Body of the Message")
	private MessageBody message;

	@ApiModelProperty(value = "Sender details")
	private NotificationSenderDto sender;

	@ApiModelProperty(value = "Request sent on")
	private Timestamp dateSent;
}
