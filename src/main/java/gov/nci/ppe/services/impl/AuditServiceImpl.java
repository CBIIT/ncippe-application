package gov.nci.ppe.services.impl;

import org.springframework.stereotype.Service;

import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEvents;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEventsClientBuilder;
import com.amazonaws.services.cloudwatchevents.model.PutEventsRequest;
import com.amazonaws.services.cloudwatchevents.model.PutEventsRequestEntry;
import com.amazonaws.services.cloudwatchevents.model.PutEventsResult;

import gov.nci.ppe.services.AuditService;

@Service
public class AuditServiceImpl implements AuditService {

	private AmazonCloudWatchEvents cloudWatchEventClient = AmazonCloudWatchEventsClientBuilder.defaultClient();

	/**
	 * {@inheritDoc}
	 * 
	 * @param eventDetails
	 * @param eventDetailType
	 */
	@Override
	public void logAuditEvent(String eventDetails, String eventDetailType) {

		PutEventsRequestEntry request_entry = new PutEventsRequestEntry().withDetail(eventDetails)
				.withDetailType(eventDetailType).withSource("aws-sdk-java-cloudwatch-example");

		PutEventsRequest request = new PutEventsRequest().withEntries(request_entry);

		PutEventsResult response = cloudWatchEventClient.putEvents(request);

	}

}
