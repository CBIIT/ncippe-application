package gov.nci.ppe.data.entity;

import java.time.LocalDateTime;
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

import gov.nci.ppe.constants.CommonConstants.LanguageOption;
import gov.nci.ppe.data.entity.dto.JsonViews;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@EqualsAndHashCode.Include
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
	private LocalDateTime dateCreated;

	@Column(name = "DateActivated", nullable = false)
	private LocalDateTime dateActivated;

	@Column(name = "DateDeactivated", nullable = false)
	private LocalDateTime dateDeactivated;

	@Column(name = "LastRevisedDate", nullable = false)
	private LocalDateTime lastRevisedDate;

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

	@Column(name = "PreferredLanguage", nullable = true)
	private LanguageOption preferredLanguage;

	public String getFullName() {
		return this.firstName + StringUtils.SPACE + this.lastName;
	}
}
