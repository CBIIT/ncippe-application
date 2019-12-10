package gov.nci.ppe.open.data.entity.dto;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.StringJoiner;
/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-11-04
 */
public class UserEnrollmentDataDTO {
	private Long trackId;
	private String protocolNumber;
	private String step;
	private String patientId;
	private String dateOfBirth;
	private String eConsentId;
	private String currentSiteCtepId;
	private String currentSiteName;
	private String currentSiteAddress;
	private Timestamp enrollmentDate;
	private String enrollmentStatus;
	private Long craCtepId;
	private String craFirstName;
	private String craLastName;
	private String craPhone;
	private String craEmail;
	private String craAddress;
	private Long treatingInvestigatorCtepId;
	private String treatingInvestigatorFirstName;
	private String treatingInvestigatorLastName;
	private String treatingInvestigatorPhone;
	private String treatingInvestigatorEmail;
	private String treatingInvestigatorAddress;
	private Long creditInvestigatorCtepId;
	private String creditInvestigatorFirstName;
	private String creditInvestigatorLastName;
	private String creditInvestigatorPhone;
	private String creditInvestigatorEmail;
	private String creditInvestigatorAddress;
	private Timestamp lastUpdateTs;
	
    @Override
    public String toString() {
        final StringJoiner sj = new StringJoiner(", ", "{ ", " }");
        sj.add(addQuotes("trackId", Objects.toString(getTrackId())));
        sj.add(addQuotes("protocolNumber", Objects.toString(getProtocolNumber())));
        sj.add(addQuotes("step", Objects.toString(getStep())));
        sj.add(addQuotes("patientId", Objects.toString(getPatientId())));
        sj.add(addQuotes("dateOfBirth", Objects.toString(getDateOfBirth())));
        sj.add(addQuotes("eConsentId", Objects.toString(geteConsentId())));
        sj.add(addQuotes("currentSiteCtepId", Objects.toString(getCurrentSiteCtepId())));
        sj.add(addQuotes("currentSiteName", Objects.toString(getCurrentSiteName())));        
        sj.add(addQuotes("currentSiteAddress", Objects.toString(getCurrentSiteAddress())));
        sj.add(addQuotes("enrollmentDate", Objects.toString(getEnrollmentDate())));
        sj.add(addQuotes("enrollmentStatus", Objects.toString(getEnrollmentStatus())));
        sj.add(addQuotes("craCtepId", Objects.toString(getCraCtepId())));
        sj.add(addQuotes("craFirstName", Objects.toString(getCraFirstName())));
        sj.add(addQuotes("craLastName", Objects.toString(getCraLastName())));
        sj.add(addQuotes("craPhone", Objects.toString(getCraPhone())));
        sj.add(addQuotes("craEmail", Objects.toString(getCraEmail())));
        sj.add(addQuotes("craAddress", Objects.toString(getCraAddress())));
        sj.add(addQuotes("treatingInvestigatorCtepId", Objects.toString(getTreatingInvestigatorCtepId())));
        sj.add(addQuotes("treatingInvestigatorFirstName", Objects.toString(getTreatingInvestigatorFirstName())));
        sj.add(addQuotes("treatingInvestigatorLastName", Objects.toString(getTreatingInvestigatorLastName())));
        sj.add(addQuotes("treatingInvestigatorPhone", Objects.toString(getTreatingInvestigatorPhone())));
        sj.add(addQuotes("treatingInvestigatorEmail", Objects.toString(getTreatingInvestigatorEmail())));
        sj.add(addQuotes("treatingInvestigatorAddress", Objects.toString(getTreatingInvestigatorAddress())));
        sj.add(addQuotes("creditInvestigatorCtepId", Objects.toString(getCreditInvestigatorCtepId())));
        sj.add(addQuotes("creditInvestigatorFirstName", Objects.toString(getCreditInvestigatorFirstName())));
        sj.add(addQuotes("creditInvestigatorLastName", Objects.toString(getCreditInvestigatorLastName())));   
        sj.add(addQuotes("creditInvestigatorPhone", Objects.toString(getCreditInvestigatorPhone())));
        sj.add(addQuotes("creditInvestigatorEmail", Objects.toString(getCreditInvestigatorEmail())));
        sj.add(addQuotes("creditInvestigatorAddress", Objects.toString(getCreditInvestigatorAddress())));   
        sj.add(addQuotes("lastUpdateTs", Objects.toString(getLastUpdateTs())));   
        
        return sj.toString();
    }	
    
    private String addQuotes(String input, String input2) {
    	return "\""+input+"\""+" : "+"\""+input2.replaceAll("[\n\r]", "")+"\"";
    }	
	
	/**
	 * @return the trackId
	 */
	public Long getTrackId() {
		return trackId;
	}
	/**
	 * @param trackId the trackId to set
	 */
	public void setTrackId(Long trackId) {
		this.trackId = trackId;
	}
	/**
	 * @return the protocolNumber
	 */
	public String getProtocolNumber() {
		return protocolNumber;
	}
	/**
	 * @param protocolNumber the protocolNumber to set
	 */
	public void setProtocolNumber(String protocolNumber) {
		this.protocolNumber = protocolNumber;
	}
	/**
	 * @return the step
	 */
	public String getStep() {
		return step;
	}
	/**
	 * @param step the step to set
	 */
	public void setStep(String step) {
		this.step = step;
	}
	/**
	 * @return the patientId
	 */
	public String getPatientId() {
		return patientId;
	}
	/**
	 * @param patientId the patientId to set
	 */
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	/**
	 * @return the dateOfBirth
	 */
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	/**
	 * @param dateOfBirth the dateOfBirth to set
	 */
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	/**
	 * @return the eConsentId
	 */
	public String geteConsentId() {
		return eConsentId;
	}
	/**
	 * @param eConsentId the eConsentId to set
	 */
	public void seteConsentId(String eConsentId) {
		this.eConsentId = eConsentId;
	}
	/**
	 * @return the currentSiteCtepId
	 */
	public String getCurrentSiteCtepId() {
		return currentSiteCtepId;
	}
	/**
	 * @param currentSiteCtepId the currentSiteCtepId to set
	 */
	public void setCurrentSiteCtepId(String currentSiteCtepId) {
		this.currentSiteCtepId = currentSiteCtepId;
	}
	/**
	 * @return the currentSiteName
	 */
	public String getCurrentSiteName() {
		return currentSiteName;
	}
	/**
	 * @param currentSiteName the currentSiteName to set
	 */
	public void setCurrentSiteName(String currentSiteName) {
		this.currentSiteName = currentSiteName;
	}
	/**
	 * @return the currentSiteAddress
	 */
	public String getCurrentSiteAddress() {
		return currentSiteAddress;
	}
	/**
	 * @param currentSiteAddress the currentSiteAddress to set
	 */
	public void setCurrentSiteAddress(String currentSiteAddress) {
		this.currentSiteAddress = currentSiteAddress;
	}
	/**
	 * @return the enrollmentDate
	 */
	public Timestamp getEnrollmentDate() {
		return enrollmentDate;
	}
	/**
	 * @param enrollmentDate the enrollmentDate to set
	 */
	public void setEnrollmentDate(Timestamp enrollmentDate) {
		this.enrollmentDate = enrollmentDate;
	}
	/**
	 * @return the enrollmentStatus
	 */
	public String getEnrollmentStatus() {
		return enrollmentStatus;
	}
	/**
	 * @param enrollmentStatus the enrollmentStatus to set
	 */
	public void setEnrollmentStatus(String enrollmentStatus) {
		this.enrollmentStatus = enrollmentStatus;
	}
	/**
	 * @return the craCtepId
	 */
	public Long getCraCtepId() {
		return craCtepId;
	}
	/**
	 * @param craCtepId the craCtepId to set
	 */
	public void setCraCtepId(Long craCtepId) {
		this.craCtepId = craCtepId;
	}
	/**
	 * @return the craFirstName
	 */
	public String getCraFirstName() {
		return craFirstName;
	}
	/**
	 * @param craFirstName the craFirstName to set
	 */
	public void setCraFirstName(String craFirstName) {
		this.craFirstName = craFirstName;
	}
	/**
	 * @return the craLastName
	 */
	public String getCraLastName() {
		return craLastName;
	}
	/**
	 * @param craLastName the craLastName to set
	 */
	public void setCraLastName(String craLastName) {
		this.craLastName = craLastName;
	}
	/**
	 * @return the craPhone
	 */
	public String getCraPhone() {
		return craPhone;
	}
	/**
	 * @param craPhone the craPhone to set
	 */
	public void setCraPhone(String craPhone) {
		this.craPhone = craPhone;
	}
	/**
	 * @return the craEmail
	 */
	public String getCraEmail() {
		return craEmail;
	}
	/**
	 * @param craEmail the craEmail to set
	 */
	public void setCraEmail(String craEmail) {
		this.craEmail = craEmail;
	}
	/**
	 * @return the craAddress
	 */
	public String getCraAddress() {
		return craAddress;
	}
	/**
	 * @param craAddress the craAddress to set
	 */
	public void setCraAddress(String craAddress) {
		this.craAddress = craAddress;
	}
	/**
	 * @return the treatingInvestigatorCtepId
	 */
	public Long getTreatingInvestigatorCtepId() {
		return treatingInvestigatorCtepId;
	}
	/**
	 * @param treatingInvestigatorCtepId the treatingInvestigatorCtepId to set
	 */
	public void setTreatingInvestigatorCtepId(Long treatingInvestigatorCtepId) {
		this.treatingInvestigatorCtepId = treatingInvestigatorCtepId;
	}
	/**
	 * @return the treatingInvestigatorFirstName
	 */
	public String getTreatingInvestigatorFirstName() {
		return treatingInvestigatorFirstName;
	}
	/**
	 * @param treatingInvestigatorFirstName the treatingInvestigatorFirstName to set
	 */
	public void setTreatingInvestigatorFirstName(String treatingInvestigatorFirstName) {
		this.treatingInvestigatorFirstName = treatingInvestigatorFirstName;
	}
	/**
	 * @return the treatingInvestigatorLastName
	 */
	public String getTreatingInvestigatorLastName() {
		return treatingInvestigatorLastName;
	}
	/**
	 * @param treatingInvestigatorLastName the treatingInvestigatorLastName to set
	 */
	public void setTreatingInvestigatorLastName(String treatingInvestigatorLastName) {
		this.treatingInvestigatorLastName = treatingInvestigatorLastName;
	}
	/**
	 * @return the treatingInvestigatorPhone
	 */
	public String getTreatingInvestigatorPhone() {
		return treatingInvestigatorPhone;
	}
	/**
	 * @param treatingInvestigatorPhone the treatingInvestigatorPhone to set
	 */
	public void setTreatingInvestigatorPhone(String treatingInvestigatorPhone) {
		this.treatingInvestigatorPhone = treatingInvestigatorPhone;
	}
	/**
	 * @return the treatingInvestigatorEmail
	 */
	public String getTreatingInvestigatorEmail() {
		return treatingInvestigatorEmail;
	}
	/**
	 * @param treatingInvestigatorEmail the treatingInvestigatorEmail to set
	 */
	public void setTreatingInvestigatorEmail(String treatingInvestigatorEmail) {
		this.treatingInvestigatorEmail = treatingInvestigatorEmail;
	}
	/**
	 * @return the treatingInvestigatorAddress
	 */
	public String getTreatingInvestigatorAddress() {
		return treatingInvestigatorAddress;
	}
	/**
	 * @param treatingInvestigatorAddress the treatingInvestigatorAddress to set
	 */
	public void setTreatingInvestigatorAddress(String treatingInvestigatorAddress) {
		this.treatingInvestigatorAddress = treatingInvestigatorAddress;
	}
	/**
	 * @return the creditInvestigatorCtepId
	 */
	public Long getCreditInvestigatorCtepId() {
		return creditInvestigatorCtepId;
	}
	/**
	 * @param creditInvestigatorCtepId the creditInvestigatorCtepId to set
	 */
	public void setCreditInvestigatorCtepId(Long creditInvestigatorCtepId) {
		this.creditInvestigatorCtepId = creditInvestigatorCtepId;
	}
	/**
	 * @return the creditInvestigatorFirstName
	 */
	public String getCreditInvestigatorFirstName() {
		return creditInvestigatorFirstName;
	}
	/**
	 * @param creditInvestigatorFirstName the creditInvestigatorFirstName to set
	 */
	public void setCreditInvestigatorFirstName(String creditInvestigatorFirstName) {
		this.creditInvestigatorFirstName = creditInvestigatorFirstName;
	}
	/**
	 * @return the creditInvestigatorLastName
	 */
	public String getCreditInvestigatorLastName() {
		return creditInvestigatorLastName;
	}
	/**
	 * @param creditInvestigatorLastName the creditInvestigatorLastName to set
	 */
	public void setCreditInvestigatorLastName(String creditInvestigatorLastName) {
		this.creditInvestigatorLastName = creditInvestigatorLastName;
	}
	/**
	 * @return the creditInvestigatorPhone
	 */
	public String getCreditInvestigatorPhone() {
		return creditInvestigatorPhone;
	}
	/**
	 * @param creditInvestigatorPhone the creditInvestigatorPhone to set
	 */
	public void setCreditInvestigatorPhone(String creditInvestigatorPhone) {
		this.creditInvestigatorPhone = creditInvestigatorPhone;
	}
	/**
	 * @return the creditInvestigatorEmail
	 */
	public String getCreditInvestigatorEmail() {
		return creditInvestigatorEmail;
	}
	/**
	 * @param creditInvestigatorEmail the creditInvestigatorEmail to set
	 */
	public void setCreditInvestigatorEmail(String creditInvestigatorEmail) {
		this.creditInvestigatorEmail = creditInvestigatorEmail;
	}
	/**
	 * @return the creditInvestigatorAddress
	 */
	public String getCreditInvestigatorAddress() {
		return creditInvestigatorAddress;
	}
	/**
	 * @param creditInvestigatorAddress the creditInvestigatorAddress to set
	 */
	public void setCreditInvestigatorAddress(String creditInvestigatorAddress) {
		this.creditInvestigatorAddress = creditInvestigatorAddress;
	}
	/**
	 * @return the lastUpdateTs
	 */
	public Timestamp getLastUpdateTs() {
		return lastUpdateTs;
	}
	/**
	 * @param lastUpdateTs the lastUpdateTs to set
	 */
	public void setLastUpdateTs(Timestamp lastUpdateTs) {
		this.lastUpdateTs = lastUpdateTs;
	}

}
