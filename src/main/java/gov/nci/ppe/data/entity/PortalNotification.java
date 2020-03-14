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

import lombok.Data;

@Data
@Entity
@Table(name = "PortalNotification")
public class PortalNotification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long portalNotificationId;

	@Column(name = "MessageEnglish", nullable = false, length = 8192)
	private String messageEnglish;

	@Column(name = "MessageSpanish", nullable = false, length = 8192)
	private String messageSpanish;

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

	@Column(name = "SubjectEnglish", nullable = true, length = 255)
	private String subjectEnglish;

	@Column(name = "SubjectSpanish", nullable = true, length = 255)
	private String subjectSpanish;

}
