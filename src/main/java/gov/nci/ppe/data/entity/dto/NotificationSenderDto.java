package gov.nci.ppe.data.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * DTO representing the User data of the sender of a Notification
 * 
 * @author PublicisSapient
 * 
 * @version 2.3
 *
 * @since Apr 30, 2021
 *
 */
@Data
@ApiModel(value = "Sender of the Notification")
public class NotificationSenderDto {

	@ApiModelProperty(value = "Sender UUID")
	private String userUUID;

	@ApiModelProperty(value = "First name of sender")
	private String firstName;

	@ApiModelProperty(value = "Last name of sender")
	private String lastName;

	@ApiModelProperty(value = "Email of sender")
	private String email;
}
