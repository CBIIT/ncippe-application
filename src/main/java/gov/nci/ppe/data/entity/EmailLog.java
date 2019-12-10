package gov.nci.ppe.data.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-13
 */
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
	private Timestamp emailRequestedOn;
	
	@Column(name="EmailSentOn", nullable = false)
	private Timestamp emailSentOn;
	
	public Long getEmailLogId() {
		return emailLogId;
	}
	public void setEmailLogId(Long emailLogId) {
		this.emailLogId = emailLogId;
	}
	public String getRecipientAddress() {
		return recipientAddress;
	}
	public void setRecipientAddress(String recipientAddress) {
		this.recipientAddress = recipientAddress;
	}
	public String getEmailSubject() {
		return emailSubject;
	}
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}
	public String getEmailBody() {
		return emailBody;
	}
	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}
	public Timestamp getEmailRequestedOn() {
		return emailRequestedOn;
	}
	public void setEmailRequestedOn(Timestamp emailRequestedOn) {
		this.emailRequestedOn = emailRequestedOn;
	}
	public Timestamp getEmailSentOn() {
		return emailSentOn;
	}
	public void setEmailSentOn(Timestamp emailSentOn) {
		this.emailSentOn = emailSentOn;
	}

}
