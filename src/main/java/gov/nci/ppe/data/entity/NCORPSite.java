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

@Entity
@Table(name = "NCORPSite")
@Data
public class NCORPSite {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long nCorpSiteId;

	@Column(name = "NCORPSiteName", nullable = true, length = 128)
	private String siteName;

	@Column(name = "CTEPId", nullable = false, length = 5)
	private String cTEPId;

	@ManyToOne
	@JoinColumn(name = "ParentSiteId")
	private NCORPSite parentSite;

	@Column(name = "LastRevisedDate", nullable = false)
	private Timestamp lastRevisedDate;

	@ManyToOne
	@JoinColumn(name = "LastRevisedUser")
	private User lastRevisedUser;

	@Column(name = "IsActive", nullable = false)
	private Boolean active;

	@Column(name = "StreetAddressLine1", nullable = false, length = 128)
	private String streetAddressLine1;

	@Column(name = "StreetAddressLine2", nullable = true, length = 128)
	private String streetAddressLine2;

	@Column(name = "City", nullable = false, length = 32)
	private String city;

	@Column(name = "State", nullable = false, length = 2)
	private String state;

	@Column(name = "ZipCode", nullable = false, length = 10)
	private String zipCode;

	@Column(name = "PhoneNumber", nullable = true, length = 10)
	private String phoneNumber;

	@Column(name = "POCName", nullable = false, length = 128)
	private String sitePOCName;

	@Column(name = "Latitude", nullable = false, length = 11)
	private String latitude;

	@Column(name = "Longitude", nullable = false, length = 11)
	private String longitude;

	public String getGpsCoordinates() {
		return latitude + "," + longitude;
	}
}
