package gov.nci.ppe.services;

import java.util.List;

import gov.nci.ppe.data.entity.ChartData;
import gov.nci.ppe.data.entity.ProjectSummary;
import gov.nci.ppe.data.entity.AgeDemographicsData;

public interface ChartDataService {
	/**
	 * Returns all Charts Data.
	 * 
	 * @return List of Chart Data with labels
	 */
	public List<ChartData> getChartData();
	
	public List<ProjectSummary> getProjectSummary();
	
	public List<AgeDemographicsData> getAgeDemographicsData();
  
}
