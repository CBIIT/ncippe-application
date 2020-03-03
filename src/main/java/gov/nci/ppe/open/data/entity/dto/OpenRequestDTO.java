package gov.nci.ppe.open.data.entity.dto;

import java.util.Objects;
import java.util.StringJoiner;

import lombok.Data;

@Data
public class OpenRequestDTO {
	private String patientId;
	private String protocolNumber;
	private String fromDate;
	private String toDate;
	
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
