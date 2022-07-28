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

import gov.nci.ppe.constants.CommonConstants.AlertContentType;
import lombok.Data;

/**
 * Entity representing an Alert record
 * 
 * @author PublicisSapient
 *
 * @version 2.6
 *
 * @since May 2, 2022
 */
@Data
@Entity
@Table(name = "Alert")
public class Alert {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "ContentType", nullable = false)
	private AlertContentType contentType;

	@Column(name = "DateCreated", nullable = false)
	private LocalDateTime dateCreated;

	@Column(name = "ExpirationDate")
	private LocalDateTime expirationDate;

	@Column(name = "MessageEnglish", nullable = false, length = 8192)
	private String messageEnglish;

	@Column(name = "MessageSpanish", nullable = false, length = 8192)
	private String messageSpanish;

	@Column(name = "LastRevisedDate", nullable = false)
	private LocalDateTime lastRevisedDate;

	@ManyToOne
	@JoinColumn(name = "LastRevisedUser", nullable = false)
	private User lastRevisedUser;
}
