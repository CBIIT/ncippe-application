package gov.nci.ppe.data.entity;

import java.time.LocalDateTime;

import gov.nci.ppe.constants.CommonConstants.NewsEventType;
import lombok.Data;

@Data
public class NewsEvent {
	private Long id;

	private NewsEventType contentType;

	private LocalDateTime date;

	private String author;

	private String link;

	private LocalDateTime lastRevisedDate;

	private Long lastRevisedUser;
}
