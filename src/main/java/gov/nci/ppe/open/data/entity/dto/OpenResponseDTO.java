package gov.nci.ppe.open.data.entity.dto;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import lombok.Data;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-11-04
 */
@Data
public class OpenResponseDTO {

	private String requestId;
	private String requestUrl;
	private String requestBy;
	private Timestamp requestDate;
	private String status;
	private String message;
	private List<UserEnrollmentDataDTO> data;
	
	
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
