package gov.nci.ppe.services.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.DescribeLogStreamsRequest;
import com.amazonaws.services.logs.model.DescribeLogStreamsResult;
import com.amazonaws.services.logs.model.InputLogEvent;
import com.amazonaws.services.logs.model.PutLogEventsRequest;
import com.amazonaws.services.logs.model.PutLogEventsResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nci.ppe.constants.CommonConstants.AuditEventType;
import gov.nci.ppe.data.entity.dto.AuditEventDto;
import gov.nci.ppe.services.AuditService;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link AuditService}
 * 
 * @author PublicisSapient
 * 
 * @version 2.3
 *
 * @since Feb 12, 2020
 *
 */
@Service
@Slf4j
public class AuditServiceImpl implements AuditService {

	private AWSLogs auditLogsClient = AWSLogsClientBuilder.defaultClient();

	private ObjectMapper mapper = new ObjectMapper();

	@Value("${audit.log.group}")
	private String logGroupName;

	@Value("${audit.log.stream}")
	private String logStreamName;

	/*
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void logAuditEvent(ObjectNode eventDetails, AuditEventType eventDetailType) throws JsonProcessingException {

		AuditEventDto auditEventDto = new AuditEventDto(eventDetailType, eventDetails);
		logAuditEvent(auditEventDto);
	}

	/*
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void logAuditEvent(AuditEventDto auditEventDto) throws JsonProcessingException {
		DescribeLogStreamsRequest logStreamsRequest = new DescribeLogStreamsRequest(logGroupName);
		logStreamsRequest.setLogStreamNamePrefix(logStreamName);
		DescribeLogStreamsResult logStreamResult = auditLogsClient.describeLogStreams(logStreamsRequest);
		String nextSequenceToken = logStreamResult.getNextToken();

		log.info("Sequence Token {}", nextSequenceToken);
		InputLogEvent auditEvent = new InputLogEvent().withMessage(mapper.writeValueAsString(auditEventDto))
				.withTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());

		PutLogEventsRequest audiEventsRequest = new PutLogEventsRequest(logGroupName, logStreamName,
				Arrays.asList(auditEvent)).withSequenceToken(nextSequenceToken);

		PutLogEventsResult response = auditLogsClient.putLogEvents(audiEventsRequest);
		log.info("Created audit event {} with status {}", response.getSdkResponseMetadata().getRequestId(),
				response.getSdkHttpMetadata().getHttpStatusCode());

	}

}
