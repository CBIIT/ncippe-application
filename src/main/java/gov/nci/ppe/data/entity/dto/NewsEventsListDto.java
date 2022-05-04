package gov.nci.ppe.data.entity.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsEventsListDto {

	private List<NewsEventDto> news;
	
	private List<NewsEventDto> events;
	
	public NewsEventsListDto() {
		this.news = new ArrayList<>();
		this.events = new ArrayList<>();
	}
}
