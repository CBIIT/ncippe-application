package gov.nci.ppe.services.impl;

public class ApiException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ApiException() {}
	
	public ApiException(String exceptionMessage) {
		super(exceptionMessage);
	}

}
