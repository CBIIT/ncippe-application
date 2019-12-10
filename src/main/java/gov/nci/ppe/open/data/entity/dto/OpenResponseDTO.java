package gov.nci.ppe.open.data.entity.dto;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-11-04
 */
public class OpenResponseDTO {

	private String requestId;
	private String requestUrl;
	private String requestBy;
	private Timestamp requestDate;
	private String status;
	private String message;
	private List<UserEnrollmentDataDTO> data;
	
	/**
	 * @return the requestId
	 */
	public String getRequestId() {
		return requestId;
	}
	
	/**
	 * @param requestId the requestId to set
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	/**
	 * @return the requestUrl
	 */
	public String getRequestUrl() {
		return requestUrl;
	}
	
	/**
	 * @param requestUrl the requestUrl to set
	 */
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	
	/**
	 * @return the requestBy
	 */
	public String getRequestBy() {
		return requestBy;
	}
	
	/**
	 * @param requestBy the requestBy to set
	 */
	public void setRequestBy(String requestBy) {
		this.requestBy = requestBy;
	}
	
	/**
	 * @return the requestDate
	 */
	public Timestamp getRequestDate() {
		return requestDate;
	}
	
	/**
	 * @param requestDate the requestDate to set
	 */
	public void setRequestDate(Timestamp requestDate) {
		this.requestDate = requestDate;
	}
	
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * @return the data
	 */
	public List<UserEnrollmentDataDTO> getData() {
		return data;
	}
	
	/**
	 * @param data the data to set
	 */
	public void setData(List<UserEnrollmentDataDTO> data) {
		this.data = data;
	}
	
    @Override
    public String toString() {
        final StringJoiner sj = new StringJoiner(", ", "{ ", " }");
        sj.add(addQuotes("requestId", Objects.toString(getRequestId())));
        sj.add(addQuotes("requestUrl", Objects.toString(getRequestUrl())));
        sj.add(addQuotes("requestBy", Objects.toString(getRequestBy())));
        sj.add(addQuotes("requestDate", Objects.toString(getRequestDate())));
        sj.add(addQuotes("status", Objects.toString(getStatus())));
        sj.add(addQuotes("message", Objects.toString(getMessage())));
        final StringJoiner data = new StringJoiner(", ");
        getData().forEach(rsp->{
        	data.add(rsp.toString());
        });
        sj.add(addList("data", data.toString()));

        return sj.toString();
    }	
    
    private String addQuotes(String input, String input2) {
    	return "\""+input+"\""+" : "+"\""+input2+"\"";
    }	
    
    private String addList(String input, String input2) {
    	return "\""+input+"\""+" : "+"["+input2+"]";
    }	    
}
