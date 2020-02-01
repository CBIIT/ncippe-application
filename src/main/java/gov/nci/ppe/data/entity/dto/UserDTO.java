package gov.nci.ppe.data.entity.dto;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import gov.nci.ppe.constants.CommonConstants.LanguageOption;
import lombok.Data;

/**
 * This class hides all the fields from USER object and display only the
 * required fields for JSON This class is populated using a Dozer object. *
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-07-22
 */

@Data
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

	@JsonProperty("lang")
	private LanguageOption preferredLanguage;

	@JsonView({ JsonViews.CrcDetailView.class, JsonViews.ProviderDetailView.class,
			JsonViews.ParticipantDetailView.class })
	private List<PortalNotificationDTO> notifications = null;

	private String portalAccountStatus;

	public UserDTO() {
	}



	@Override
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
