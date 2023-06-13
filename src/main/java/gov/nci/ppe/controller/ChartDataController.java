package gov.nci.ppe.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.nci.ppe.constants.UrlConstants;
import gov.nci.ppe.data.entity.Alert;
import gov.nci.ppe.data.entity.NewsEvent;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Logger;

@RestController
public class ChartDataController
{
    private Logger logger = Logger.getLogger(ChartDataController.class.getName());


    @ApiOperation(value = "Method to return data for charts")
    @GetMapping(value = UrlConstants.CHART_DATA)
    public ResponseEntity<String> getChartData()
    {
        logger.info("MHL ChartDataController: {\"data\": \"MHL test data\"}");
        System.out.println("MHL ChartDataController: {\"data\": \"MHL test data\"}");
        System.err.println("MHL ChartDataController: {\"data\": \"MHL test data\"}");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        System.out.println("MHL IN NewsController");
        String responseString = "{\"data\": \"MHL test data\"}";
        System.out.println(responseString);
        return new ResponseEntity<String>(responseString, httpHeaders, HttpStatus.OK);

        // return new ResponseEntity<String>("{\"data\": \"MHL test data\"}", httpHeaders, HttpStatus.OK);
       // return new ResponseEntity<String>("{\"data\": \"MHL test data\"}", httpHeaders, HttpStatus.OK);
    }

}
