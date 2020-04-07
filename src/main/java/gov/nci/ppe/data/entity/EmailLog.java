package gov.nci.ppe.data.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-13
 */
@Data
@Entity
@Table(name = "EmailLog")
public class EmailLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private Long emailLogId;
	
	@Column(name="RecipientAddress", nullable = false, length = 127)
	private String recipientAddress; 
	
	@Column(name="Subject", nullable = false, length = 256)
	private String emailSubject;
	
	@Column(name="Body", nullable = false, length = 8192)
	private String emailBody;
	
	@Column(name="EmailRequestedOn", nullable = false)
	private LocalDateTime emailRequestedOn;
	
	@Column(name="EmailSentOn", nullable = false)
	private LocalDateTime emailSentOn;
	

}
