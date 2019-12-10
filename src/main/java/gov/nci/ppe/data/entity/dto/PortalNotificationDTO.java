package gov.nci.ppe.data.entity.dto;

import java.sql.Timestamp;

public class PortalNotificationDTO {
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Timestamp getDateGenerated() {
		return dateGenerated;
	}

	public void setGeneratedDate(Timestamp dateGenerated) {
		this.dateGenerated = dateGenerated;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public int getViewedByUser() {
		return viewedByUser;
	}

	public void setViewedByUser(int viewedByUser) {
		this.viewedByUser = viewedByUser;
	}


	public String getMessageFrom() {
		return messageFrom;
	}

	public void setMessageFrom(String messageFrom) {
		this.messageFrom = messageFrom;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	private String message;
	
	private Timestamp dateGenerated;
	
	private Long userId;
	
	private int viewedByUser;
	
	private String messageFrom;
	
	private String subject;

}
