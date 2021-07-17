package gov.nci.ppe.data.entity.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nci.ppe.constants.CommonConstants.AuditEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Dto representing an Audit record
 * 
 * @author D. Sarkar
 * 
 * @version 2.4
 * 
 * @since Jul 16, 2021
 */
@Getter
@AllArgsConstructor
public class AuditEventDto {
    private AuditEventType eventType;
    private ObjectNode eventDetail;
}
