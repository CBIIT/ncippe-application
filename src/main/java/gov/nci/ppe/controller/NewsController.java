package gov.nci.ppe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nci.ppe.constants.UrlConstants;
import gov.nci.ppe.data.entity.NewsEvent;
import gov.nci.ppe.data.entity.dto.NewsEventsListDto;
import gov.nci.ppe.services.NewsEventsService;
import io.swagger.annotations.ApiOperation;

/**
 * Rest Endpoint for returning News and Events
 * @author PublicisSapient
 * @version 2.6
 * @since Mar 22, 2022
 */

 @RestController
public class NewsController {

    private NewsEventsService newsEventsService;

    @Autowired
    public NewsController(NewsEventsService newsEventsService) {
        this.newsEventsService = newsEventsService;
    }

    @ApiOperation( value = "Return all unexpired news and events")
    @GetMapping(value = UrlConstants.URL_NEWS_EVENTS)
    public ResponseEntity<String> getActiveNewsAndEvents() throws JsonProcessingException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        List<NewsEvent> newsEvents  = newsEventsService.getActiveNewsEvents();
        String responseString = convertToString(newsEvents);
        System.out.println(responseString); 
        return new ResponseEntity<String>(responseString, httpHeaders, HttpStatus.OK);
    }

	private String convertToString(List<NewsEvent> newsEvents) throws JsonProcessingException {
		NewsEventsListDto newsEventsList = new NewsEventsListDto();
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(newsEventsList);
	}
}
