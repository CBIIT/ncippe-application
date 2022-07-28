package gov.nci.ppe.data.entity.dto;

import java.sql.Timestamp;

import gov.nci.ppe.constants.CommonConstants.AlertContentType;
import lombok.Data;

/** 
 * DTO object representing an alert
 */
@Data
public class AlertDto {
    private Integer id;
    private AlertContentType contentType;
    private Timestamp dateCreated;
    private Timestamp expirationDate;
    private MessageBody message;
}
