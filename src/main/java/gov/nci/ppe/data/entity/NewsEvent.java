package gov.nci.ppe.data.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import gov.nci.ppe.constants.CommonConstants.NewsEventType;
import lombok.Data;

@Data
@Entity
@Table(name = "NewsAndEvents")
public class NewsEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "ContentType", nullable = false)
	private NewsEventType contentType;

	@Column(name = "ExpirationDate", nullable = false)
	private LocalDateTime expirationDate;

	@Column(name = "Author", nullable = true, length = 128)
	private String author;

	@Column(name = "Link", nullable = false, length = 1024)
	private String link;

	@Column(name = "LastRevisedDate", nullable = false)
	private LocalDateTime lastRevisedDate;

	@ManyToOne
	@JoinColumn(name = "LastRevisedUser", nullable = false)
	private User lastRevisedUser;
}
