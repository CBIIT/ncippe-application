package gov.nci.ppe.data.entity.dto;

import java.sql.Timestamp;

import io.swagger.annotations.ApiModel;
import lombok.Data;

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
	private class Message {
		private String en;
		private String es;
	}

	@Data
	private class Subject {
		private String en;
		private String es;
	}

}
