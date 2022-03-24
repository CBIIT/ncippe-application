package gov.nci.ppe.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nci.ppe.data.entity.NewsEvent;
import gov.nci.ppe.data.repository.NewsEventsRepository;
import gov.nci.ppe.services.NewsEventsService;

/**
 * Implementation of {@link NewsEventsService}
 * 
 * @author PublicisSapient
 * 
 * @version 2.6
 * 
 * @since 222-03-24
 *
 */
@Service
public class NewsEventsServiceImpl implements NewsEventsService {

	private NewsEventsRepository newsEventsRepository;

	@Autowired
	public NewsEventsServiceImpl(NewsEventsRepository newsEventsRepository) {
		this.newsEventsRepository = newsEventsRepository;
	}

	@Override
	public List<NewsEvent> getActiveNewsEvents() {
		return newsEventsRepository.findByExpirationDateBefore(LocalDateTime.now());
	}

}
