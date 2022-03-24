package gov.nci.ppe.services;

import java.util.List;

import gov.nci.ppe.data.entity.NewsEvent;

/**
 * Interface defining methods related to News and events
 * 
 * @author PublicisSapient
 * @version 2.6;
 * @since 2022-03-24
 */
public interface NewsEventsService {

	/**
	 * Get all unexpired {@link NewsEvent} records
	 * 
	 * @return list of NewsEvent records
	 */
	List<NewsEvent> getActiveNewsEvents();

}
