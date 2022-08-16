package gov.nci.ppe.exception;

/**
 * Exception thrown when the request would violate a Business rule
 * 
 * @author PublicisSapient
 *
 * @version 2.6
 *
 * @since Jul 28, 2022
 */
public class BusinessConstraintViolationException extends Exception {

	public BusinessConstraintViolationException(String message) {
		super(message);
	}
}
