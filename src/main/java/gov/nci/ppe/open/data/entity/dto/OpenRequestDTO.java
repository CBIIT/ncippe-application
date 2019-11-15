package gov.nci.ppe.open.data.entity.dto;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.StringJoiner;

public class OpenRequestDTO {
	private String patientId;
	private String protocolNumber;
	private String fromDate;
	private String toDate;
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
	 * @return the fromDate
	 */
	public String getFromDate() {
		return fromDate;
	}
	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	/**
	 * @return the toDate
	 */
	public String getToDate() {
		return toDate;
	}
	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	
    @Override
    public String toString() {
        final StringJoiner sj = new StringJoiner(", ", "{ ", " }");
        sj.add(addQuotes("patientId", Objects.toString(getPatientId())));
        sj.add(addQuotes("protocolNumber", Objects.toString(getProtocolNumber())));
        sj.add(addQuotes("fromDate", Objects.toString(getFromDate())));
        sj.add(addQuotes("toDate", Objects.toString(getToDate())));

        return sj.toString();
    }	
    
    private String addQuotes(String input, String input2) {
    	return "\""+input+"\""+" : "+"\""+input2+"\"";
    }
}
