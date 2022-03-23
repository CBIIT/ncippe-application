package gov.nci.ppe.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import gov.nci.ppe.constants.UrlConstants;
import io.swagger.annotations.ApiOperation;

/**
 * Rest Endpoint for returning News and Events
 * @author PublicisSapient
 * @version 2.6
 * @since Mar 22, 2022
 */

 @RestController
public class NewsController {

    @ApiOperation( value = "Return all unexpired news and events")
    @GetMapping(value = UrlConstants.URL_NEWS_EVENTS)
    public ResponseEntity<String> getActiveNewsAndEvents() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        String responseString = "";
        return new ResponseEntity<String>(responseString, httpHeaders, HttpStatus.OK);
    }
}
