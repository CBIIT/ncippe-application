package gov.nci.ppe.data.entity.dto;

import java.sql.Timestamp;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * DTO representing a Portal Notification object
 * 
 * @author PublicisSapient
 * 
 * @version 1.0
 *
 * @since Mar 14, 2020
 *
 */
@ApiModel(description = "Portal Notification")
@Data
public class PortalNotificationDTO {

	private Message message = new Message();

	private Timestamp dateGenerated;

	private Long userId;

	private int viewedByUser;

	private String messageFrom;

	private Subject subject = new Subject();

	@Data
	public class Message {
		private String en;
		private String es;
	}

	@Data
	public class Subject {
		private String en;
		private String es;
	}

}
