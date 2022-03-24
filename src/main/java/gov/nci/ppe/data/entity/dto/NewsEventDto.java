package gov.nci.ppe.data.entity.dto;

import java.sql.Timestamp;

import lombok.Data;

/**
 * DTO object for News and Events record
 * 
 * @author sarkard
 *
 */

@Data
public class NewsEventDto {

	private Long id;

	private String contentType;

	private Timestamp expirationDate;

	private String author;

	private String link;
}
