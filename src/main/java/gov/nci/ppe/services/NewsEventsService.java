package gov.nci.ppe.services;

import java.util.List;

import gov.nci.ppe.data.entity.NewsEvent;

public interface NewsEventsService {

	/**
	 * Get all unexpired {@link NewsEvent} records
	 * @return list of NewsEvent records
	 */
	List<NewsEvent> getActiveNewsEvents();
    
}
