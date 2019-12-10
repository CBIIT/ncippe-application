package gov.nci.ppe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
	@GetMapping("/healthcheck")
	public ResponseEntity<String> isHealthy() {
		return ResponseEntity.ok("Healthy");
	}
}
