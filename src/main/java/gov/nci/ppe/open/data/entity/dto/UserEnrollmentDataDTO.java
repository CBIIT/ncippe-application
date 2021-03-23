package gov.nci.ppe.open.data.entity.dto;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Objects;
import java.util.StringJoiner;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-11-04
 */
@Data
public class UserEnrollmentDataDTO {
	private Long trackId;
	private String protocolNumber;
	private String step;
	private String patientId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	private LocalDate dateOfBirth;
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
		sj.add(addQuotes("eConsentId", Objects.toString(getEConsentId())));
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
	

}
