package gov.nci.ppe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.nci.ppe.configurations.EmailServiceConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import gov.nci.ppe.services.EmailLogService;

/**
 * Controller class for Email related actions.
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-15
 */
@Api(value = "emailService")
@RestController
@RequestMapping(value = "/emailService")
public class EmailServiceController {

	@Autowired
	private EmailLogService emailLogService;

	@Autowired
	private EmailServiceConfig emailServiceConfig;

	/**
	 * This method will be triggered post uploading participant report.
	 * 
	 * @param firstName      - Recipient's first name which will be inserted into
	 *                       the email.
	 * @param recipientEmail - Recipient's email address
	 * @return HTTP Response
	 */
	@ApiOperation(value = "Send Notification Email after uploading report")
	@PostMapping(value = "/sendEmailForUploadedReport")
	public ResponseEntity<String> sendEmailForUploadedReport(
			@ApiParam(value = "Recipient's first name which will be inserted into the email", required = true) @RequestParam(value = "firstName", required = true) String firstName,
			@ApiParam(value = "Recipient's email address", required = true) @RequestParam(value = "recipientEmailId", required = true) String recipientEmail) {

		String response = emailLogService.sendEmailAfterUploadingReport(firstName, recipientEmail, firstName,
				emailServiceConfig.getSenderEmailAddress(), emailServiceConfig.getEmailUploadFileSubject(),
				emailServiceConfig.getEmailUploadFileHTMLBody(), emailServiceConfig.getEmailUploadFileTextBody());
		return ResponseEntity.status(HttpStatus.OK).body("{\n" + response + "\n}");
	}

}
