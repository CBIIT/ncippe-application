package gov.nci.ppe.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.nci.ppe.constants.UrlConstants;
import gov.nci.ppe.data.entity.Alert;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChartDataController
{

    @ApiOperation(value = "Method to return data for charts")
    @GetMapping(value = UrlConstants.CHART_DATA, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getChartData() throws JsonProcessingException
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set( "Content-Type", MediaType.APPLICATION_JSON_VALUE);
        System.out.println("MHL ChartDataController: {\"data\": \"MHL test data\"}");
        return new ResponseEntity<String>("{\"data\": \"MHL test data\"}", httpHeaders, HttpStatus.OK);
    }

}
