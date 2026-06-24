package gov.nci.ppe.data.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Sender of the Notification")
public class NotificationSenderDto {

	@Schema(description = "Sender UUID")
	private String userUUID;

	@Schema(description = "First name of sender")
	private String firstName;

	@Schema(description = "Last name of sender")
	private String lastName;

	@Schema(description = "Email of sender")
	private String email;
}
