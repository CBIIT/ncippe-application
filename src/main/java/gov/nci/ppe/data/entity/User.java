package gov.nci.ppe.data.entity;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DiscriminatorOptions;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonView;

import gov.nci.ppe.data.entity.dto.JsonViews;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-07-22
 */
@Entity
@Table(name = "User")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorOptions(force = true)
@DiscriminatorColumn(name = "UserType", discriminatorType = DiscriminatorType.INTEGER)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(name = "UserUUID", nullable = true, length = 36)
	private String userUUID;

	@Column(name = "FirstName", nullable = true, length = 45)
	private String firstName;

	@Column(name = "LastName", nullable = true, length = 45)
	private String lastName;

	@Column(name = "Email", nullable = true, length = 45)
	private String email;

	@Column(name = "PhoneNumber", nullable = true, length = 10)
	private String phoneNumber;

	@Column(name = "AllowEmailNotification", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean allowEmailNotification;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RoleId")
	@Fetch(FetchMode.JOIN)
	private Role role;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserType", insertable = false, updatable = false)
	@Fetch(FetchMode.JOIN)
	private Code userType;

	@Column(name = "DateCreated", nullable = false)
	private Timestamp dateCreated;

	@Column(name = "DateActivated", nullable = false)
	private Timestamp dateActivated;

	@Column(name = "DateDeactivated", nullable = false)
	private Timestamp dateDeactivated;

	@Column(name = "LastRevisedDate", nullable = false)
	private Timestamp lastRevisedDate;

	@Column(name = "LastRevisedUser", nullable = true)
	private Long lastRevisedUser;

	/* This will hold the notifications for the user */
	@JsonView({ JsonViews.ParticipantDetailView.class, JsonViews.CrcDetailView.class,
			JsonViews.ProviderDetailView.class })
	@OneToMany(mappedBy = "userNotification")
	@OrderBy("DateGenerated DESC")
	List<PortalNotification> notifications;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PortalAccountStatusCodeId")
	@Fetch(FetchMode.JOIN)
	private Code portalAccountStatus;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserUUID() {
		return userUUID;
	}

	public void setUserUUID(String userUUID) {
		this.userUUID = userUUID;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean getAllowEmailNotification() {
		return allowEmailNotification;
	}

	public void setAllowEmailNotification(boolean allowEmailNotification) {
		this.allowEmailNotification = allowEmailNotification;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Code getUserType() {
		return userType;
	}

	public void setUserType(Code code) {
		this.userType = code;
	}

	public Timestamp getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Timestamp dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Timestamp getDateActivated() {
		return dateActivated;
	}

	public void setDateActivated(Timestamp dateActivated) {
		this.dateActivated = dateActivated;
	}

	public Timestamp getDateDeactivated() {
		return dateDeactivated;
	}

	public void setDateDeactivated(Timestamp dateDeactivated) {
		this.dateDeactivated = dateDeactivated;
	}

	public Timestamp getLastRevisedDate() {
		return lastRevisedDate;
	}

	public void setLastRevisedDate(Timestamp lastRevisedDate) {
		this.lastRevisedDate = lastRevisedDate;
	}

	public Long getLastRevisedUser() {
		return lastRevisedUser;
	}

	public void setLastRevisedUser(Long lastRevisedUser) {
		this.lastRevisedUser = lastRevisedUser;
	}

	public List<PortalNotification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<PortalNotification> notifications) {
		this.notifications = notifications;
	}

	/**
	 * @return the portalAccountStatus
	 */
	public Code getPortalAccountStatus() {
		return portalAccountStatus;
	}

	/**
	 * @param portalAccountStatus the portalAccountStatus to set
	 */
	public void setPortalAccountStatus(Code portalAccountStatus) {
		this.portalAccountStatus = portalAccountStatus;
	}

	public String getFullName() {
		return this.firstName + StringUtils.SPACE + this.lastName;
	}
}
