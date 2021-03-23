package gov.nci.ppe.data.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * DTO for generating Site Information
 * 
 * @author PublicisSapient
 * 
 * @version 2.0
 *
 * @since Jan 29, 2020
 *
 */
@Data
public class NCORPSiteDTO {

	@JsonProperty("title")
	private String siteName;

	@JsonProperty("address_1")
	private String streetAddressLine1;

	@JsonProperty("address_2")
	private String streetAddressLine2;

	private String city;

	private String state;

	@JsonProperty("zip")
	private String zipCode;

	@JsonProperty("poc")
	private String sitePOCName;

	@JsonProperty("telephone")
	private String phoneNumber;

	@JsonProperty("extension")
	private String phoneNumberExtension;

	@JsonProperty("poc_email")
	private String pocEmail;

	@JsonProperty("gps_coordinates")
	private String gpsCoordinates;

	@JsonProperty("website")
	private String website;

}
