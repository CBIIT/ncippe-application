package gov.nci.ppe.data.entity.dto;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * This class hides all the fields from USER object and display only the
 * required fields for JSON This class is populated using a Dozer object. *
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-07-22
 */

public class UserDTO {

	private String firstName;

	private String lastName;

	private String email;

	private String uuid;

	private String phoneNumber;

	private boolean allowEmailNotification;

	private String userType;

	private String roleName;

	private Timestamp dateCreated;

	private Timestamp dateActivated;

	private Timestamp dateDeactivated;

	@JsonView({ JsonViews.CrcDetailView.class, JsonViews.ProviderDetailView.class,
			JsonViews.ParticipantDetailView.class })
	private List<PortalNotificationDTO> notifications = null;

	private String portalAccountStatus;

	public UserDTO() {
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String userUUID) {
		this.uuid = userUUID;
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

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
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

	public List<PortalNotificationDTO> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<PortalNotificationDTO> notifications) {
		this.notifications = notifications;
	}

	public String toString() {
		StringBuilder retValue = new StringBuilder();
		retValue.append(StringUtils.CR).append(",").append(StringUtils.CR).append(" First Name : ").append(firstName)
				.append(",").append(StringUtils.CR).append(" Last Name : ").append(lastName).append(",")
				.append(StringUtils.CR).append(" Email Id : ").append(email).append(",").append(StringUtils.CR)
				.append(" User UUID : ").append(uuid);
		return retValue.toString();
	}

	/**
	 * @return the portalAccountStatus
	 */
	public String getPortalAccountStatus() {
		return portalAccountStatus;
	}

	/**
	 * @param portalAccountStatus the portalAccountStatus to set
	 */
	public void setPortalAccountStatus(String portalAccountStatus) {
		this.portalAccountStatus = portalAccountStatus;
	}

	public Timestamp getDateDeactivated() {
		return dateDeactivated;
	}

	public void setDateDeactivated(Timestamp dateDeactivated) {
		this.dateDeactivated = dateDeactivated;
	}
}
