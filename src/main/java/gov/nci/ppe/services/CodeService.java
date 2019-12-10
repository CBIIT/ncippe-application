package gov.nci.ppe.services;

import gov.nci.ppe.data.entity.Code;

/**
 * This is a Service class that orchestrates all the calls to Code table
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-09-09
 */
public interface CodeService {
	
	public Code getCode(String codeName);

}
