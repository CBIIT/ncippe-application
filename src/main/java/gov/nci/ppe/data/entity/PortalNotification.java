package gov.nci.ppe.data.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PortalNotification")
public class PortalNotification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long portalNotificationId;

	@Column(name = "Message", nullable = false, length = 8192)
	private String message;

	@Column(name = "DateGenerated", nullable = false)
	private Timestamp dateGenerated;

	@Column(name = "UserId", nullable = false, length = 11)
	private Long userId;

	@Column(name = "ViewedByUser", nullable = false, length = 4)
	private int viewedByUser;

	@ManyToOne(targetEntity = User.class)
	@JoinColumn(name = "UserId", insertable = false, updatable = false)
	private User userNotification;

	@Column(name = "MessageFrom", nullable = true, length = 45)
	private String messageFrom;

	@Column(name = "Subject", nullable = true, length = 255)
	private String subject;

	public Long getPortalNotificationId() {
		return portalNotificationId;
	}

	public void setPortalNotificationId(Long portalNotificationId) {
		this.portalNotificationId = portalNotificationId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Timestamp getDateGenerated() {
		return dateGenerated;
	}

	public void setDateGenerated(Timestamp dateGenerated) {
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

	public User getUserNotification() {
		return userNotification;
	}

	public void setUserNotification(User userNotification) {
		this.userNotification = userNotification;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessageFrom() {
		return messageFrom;
	}

	public void setMessageFrom(String messageFrom) {
		this.messageFrom = messageFrom;
	}

}
