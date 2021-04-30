package gov.nci.ppe.data.entity;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

/**
 * Entity representing a group notification request
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2021-04-28
 */
@Data
@Entity
@Table(name = "GroupNotificationRequest")
public class GroupNotificationRequest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long requestId;

	@ManyToOne(targetEntity = User.class)
	@JoinColumn(name = "RequesterId", insertable = false, updatable = false)
	private User requester;

	@Column(name = "RequestDateTime", nullable = false)
	private LocalDateTime timeOfRequest;

	@ManyToMany(targetEntity = Role.class)
	@JoinTable(name = "GroupNotificationRecipientRole", joinColumns = @JoinColumn(name = "RequestId"), inverseJoinColumns = @JoinColumn(name = "RoleId"))
	private Set<Role> recipientRoles;
}
