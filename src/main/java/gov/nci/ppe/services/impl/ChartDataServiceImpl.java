package gov.nci.ppe.services.impl;

import gov.nci.ppe.data.repository.ChartDataRepository;
import gov.nci.ppe.data.repository.ProjectSummaryRepository;
import gov.nci.ppe.data.repository.AgeDemographicsDataRepository;
import gov.nci.ppe.services.ChartDataService;
import gov.nci.ppe.data.entity.AgeDemographicsData;
import gov.nci.ppe.data.entity.ChartData;
import gov.nci.ppe.data.entity.ProjectSummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChartDataServiceImpl implements ChartDataService {

    private ChartDataRepository chartDataRepository;
    
    private ProjectSummaryRepository projectSummaryRepository;
    
    private AgeDemographicsDataRepository ageDemporaphicsDataRepository;

    @Autowired
    public ChartDataServiceImpl(ChartDataRepository chartDataRepository, ProjectSummaryRepository projectSummaryRepository,
    		AgeDemographicsDataRepository ageDemographicsDataRepository) {
        this.chartDataRepository = chartDataRepository;
        
        this.projectSummaryRepository = projectSummaryRepository;
        
        this.ageDemporaphicsDataRepository = ageDemographicsDataRepository;
    }

//
//
//    private String testData = "{" +
//            " \"projectSummary\": [ { \"label\": \"charts.chart_data.ProjectSummary.ParticipantsEnrolled\", \"value\": 27}, { \"label\": \"charts.chart_data.ProjectSummary.SitesThatHaveEnrolledParticipants\", \"value\": 21}, { \"label\": \"charts.chart_data.ProjectSummary.CancerTypes\", \"value\": 18}, { \"label\": \"charts.chart_data.ProjectSummary.BiomarkerTestReturned\", \"value\": 16}], " +
//            " \"patientDemographicsByCancerType\": [    {      \"label\": \"charts.chart_data.PatientDemographics.ColonCancer.label\",      \"value\": 26    },    {      \"label\": \"charts.chart_data.PatientDemographics.LungCancer.label\",      \"value\": 21    },    {      \"label\": \"charts.chart_data.PatientDemographics.ProstateCancer.label\",      \"value\": 18    },    {      \"label\": \"charts.chart_data.PatientDemographics.Melanoma.label\",      \"value\": 16    },    {      \"label\": \"charts.chart_data.PatientDemographics.GastroesophagealCancer.label\",      \"value\": 9    },    {      \"label\": \"charts.chart_data.PatientDemographics.MultipleMyeloma.label\",      \"value\": 4    },    {      \"label\": \"charts.chart_data.PatientDemographics.Leukemia.label\",      \"value\": 6    },    {      \"label\": \"charts.chart_data.PatientDemographics.AcuteMyeloid.label\",      \"value\": 1    }  ],  \"participantDemographicsAge\": [    {      \"label\": \"34-43\",      \"value\": 38    },    {      \"label\": \"44-53\",      \"value\": 15    },    {      \"label\": \"54-63\",      \"value\": 23    },    {      \"label\": \"64-73\",      \"value\": 8    },    {      \"label\": \"74-83\",      \"value\": 15    },    {      \"label\": \"84-93\",      \"value\": 2    }  ],  \"participantDemographicsSex\": [    {      \"label\": \"Male\",      \"value\": 62    },    {      \"label\": \"Female\",      \"value\": 38    }  ],  \"patientDemographicsRace\": [    {      \"label\": \"charts.chart_data.PatientDemographicsRace.White.label\",      \"value\": 74    },    {      \"label\": \"charts.chart_data.PatientDemographicsRace.BlackOrAfricanAmerican.label\",      \"value\": 18    },    {      \"label\": \"charts.chart_data.PatientDemographicsRace.Asian.label\",      \"value\": 3    },    {      \"label\": \"charts.chart_data.PatientDemographicsRace.Unknown.label\",      \"value\": 3    },    {      \"label\": \"charts.chart_data.PatientDemographicsRace.NotReported.label\",      \"value\": 1    },    {      \"label\": \"charts.chart_data.PatientDemographicsRace.NativeHawaiianOrOtherPacificIslander.label\",      \"value\": 1    }  ],  \"patientDemographicsEthnicity\": [    {      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.NotHispanicOrLatino.label\",      \"value\": 94    },    {      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.HispanicOrLatino.label\",      \"value\": 4    },    {      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.Unknown.label\",      \"value\": 1    },    {      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.NotReported.label\",      \"value\": 1    }  ]}\n";
/*

    public ChartDataServiceImpl(ChartDataService chartDataService) {
        this.chartDataService = chartDataService;
    }

    public String getChartData(ChartDataRepository chartDataRepo) {
        return testData;
    }

*/
    @Override
    public List<ChartData> getChartData() {
        System.out.println("MHL getChartData");
       
        return chartDataRepository.findAll();
    }

    @Override
	public List<ProjectSummary> getProjectSummary() {
		return projectSummaryRepository.findAll();
	}

	@Override
	public List<AgeDemographicsData> getAgeDemographicsData() {
		// TODO Auto-generated method stub
		return ageDemporaphicsDataRepository.findAll();
	}
}
