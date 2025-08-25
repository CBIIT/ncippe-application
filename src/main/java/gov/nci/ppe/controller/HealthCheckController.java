package gov.nci.ppe.controller;

import java.util.stream.Collectors;
import org.springframework.web.util.HtmlUtils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import gov.nci.ppe.constants.UrlConstants;
import io.swagger.annotations.ApiOperation;

/**
 * Health Check controller - provides a simple ReST endpoint for Application
 * Load Balancer to check
 * 
 * @author debsarka0
 *
 */

@RestController
public class HealthCheckController {

	@ApiOperation(value = "Healthcheck operation. Returns Healthy if server is working")
	@GetMapping(UrlConstants.URL_HEALTHCHECK)
	public ResponseEntity<String> isHealthy() {
		return ResponseEntity.ok("Healthy");
	}

	@GetMapping("/api/v1/requestHeaders")
	public ResponseEntity<String> multiValue(@RequestHeader MultiValueMap<String, String> headers) {
		StringBuilder headerString = new StringBuilder();
		headers.forEach((key, value) -> {
			String escapedKey = HtmlUtils.htmlEscape(key);
			String escapedValues = value.stream()
					.map(HtmlUtils::htmlEscape)
					.collect(Collectors.joining("|"));
			headerString.append(String.format("Header '%s' = %s%n", escapedKey, escapedValues));
            System.out.println("MHL headerString: " + headerString.toString());
		});

		return new ResponseEntity<>(headerString.toString(), HttpStatus.OK);
	}
}
