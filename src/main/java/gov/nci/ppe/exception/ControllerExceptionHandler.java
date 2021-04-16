package gov.nci.ppe.exception;

import java.util.UUID;

import javax.validation.ConstraintViolationException;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * Exception Handler to capture exceptions thrown from ReST Controllers
 * 
 */
@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
	private static final String ERROR_ID = "errorId";

	/**
	 * Handle Validation exceptions throw when input is invalid
	 * 
	 * @param t
	 * @return
	 */
	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<?> handleInputValidationException(ConstraintViolationException t) {
		return handleErrorResponse(t, t.getMessage(), HttpStatus.BAD_REQUEST);
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		return handleErrorResponse(ex, ex.getMessage(), status);
	}

	protected <T> ResponseEntity<T> createResponse(T body, HttpStatus httpStatus) {
		log.debug("Responding with status {} ", httpStatus);
		return new ResponseEntity<T>(body, new HttpHeaders(), httpStatus);
	}

	protected ResponseEntity<Object> handleErrorResponse(Throwable throwable, String message, HttpStatus httpStatus) {
		final String errorId = UUID.randomUUID().toString();
		MDC.put(ERROR_ID, errorId);

		if (throwable == null) {
			message = "Unknown error caught in ReSTController";
			log.error(String.format("%s, {}", message), httpStatus);
		}

		log.error("Error caught: %s", message, throwable);

		MDC.remove(ERROR_ID);
		return createResponse(message, httpStatus);
	}
}
