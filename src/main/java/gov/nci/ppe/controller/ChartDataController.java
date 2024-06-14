package gov.nci.ppe.controller;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.MappingException;

import gov.nci.ppe.constants.CommonConstants;
import gov.nci.ppe.constants.UrlConstants;
import gov.nci.ppe.data.repository.AgeDemographicsDataRepository;
import gov.nci.ppe.data.repository.ChartDataRepository;
import gov.nci.ppe.data.repository.ProjectSummaryRepository;
import gov.nci.ppe.services.ChartDataService;
import gov.nci.ppe.data.entity.AgeDemographicsData;
import gov.nci.ppe.data.entity.ChartData;
import gov.nci.ppe.data.entity.ProjectSummary;
import gov.nci.ppe.data.entity.dto.AgeDemographicsDataDTO;
import gov.nci.ppe.data.entity.dto.ChartDataDTO;
import gov.nci.ppe.data.entity.dto.ProjectSummaryDTO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.json.JsonObject;

/*
**  Implement the REST API endpoint to return all data for Charts in StudyProgress Page
** 	include : projectSummary, 
**			 Age Demographics data without chart labels
**			 All data with chart-labels (chart-labels match to the key value in ncippe-content in BabelEdit)			
**
**
**  @since Jun 2024
**/		

@RestController
public class ChartDataController {
    private Logger logger = Logger.getLogger(ChartDataController.class.getName());

    @Autowired
    private ChartDataRepository chartDataRepository;
    
    @Autowired
    private AgeDemographicsDataRepository ageDemographicsDataRepository;
    
    @Autowired
    private ProjectSummaryRepository projectSummaryRepository;
    
    @Autowired
    private ChartDataService chartDataService;
    
    @Autowired
	@Qualifier("dozerBean")
	private Mapper dozerBeanMapper;

    @JsonValue
    String testData  = "{" +
            " \"projectSummary\": [ { \"label\": \"charts.chart_data.ProjectSummary.ParticipantsEnrolled\", \"value\": 27}, "
            + "{ \"label\": \"charts.chart_data.ProjectSummary.SitesThatHaveEnrolledParticipants\", \"value\": 21}, "
            + "{ \"label\": \"charts.chart_data.ProjectSummary.CancerTypes\", \"value\": 18}, "
            + "{ \"label\": \"charts.chart_data.ProjectSummary.BiomarkerTestReturned\", \"value\": 16}], " +
            "  \"patientDemographicsByCancerType\": [    "
            + "{      \"label\": \"charts.chart_data.PatientDemographics.ColonCancer.label\",      \"value\": 26    },    "
            + "{      \"label\": \"charts.chart_data.PatientDemographics.LungCancer.label\",      \"value\": 21    },    "
            + "{      \"label\": \"charts.chart_data.PatientDemographics.ProstateCancer.label\",      \"value\": 18    },    "
            + "{      \"label\": \"charts.chart_data.PatientDemographics.Melanoma.label\",      \"value\": 16    },    "
            + "{      \"label\": \"charts.chart_data.PatientDemographics.GastroesophagealCancer.label\",      \"value\": 9    },    "
            + "{      \"label\": \"charts.chart_data.PatientDemographics.MultipleMyeloma.label\",      \"value\": 4    },    "
            + "{      \"label\": \"charts.chart_data.PatientDemographics.Leukemia.label\",      \"value\": 6    },    "
            + "{      \"label\": \"charts.chart_data.PatientDemographics.AcuteMyeloid.label\",      \"value\": 1    }  ], " 
            + " \"participantDemographicsAge\": [    "
            + "{      \"label\": \"34-43\",      \"value\": 38    },    "
            + "{      \"label\": \"44-53\",      \"value\": 15    },    "
            + "{      \"label\": \"54-63\",      \"value\": 23    },    {      \"label\": \"64-73\",      \"value\": 8    },    "
            + "{      \"label\": \"74-83\",      \"value\": 15    },    {      \"label\": \"84-93\",      \"value\": 2    }  ],  " 
            + " \"participantDemographicsSex\": [    " 
            + "{      \"label\": \"Male\",      \"value\": 62    },    {      \"label\": \"Female\",      \"value\": 38    }  ],  " 
            + " \"patientDemographicsRace\": [    {      \"label\": \"charts.chart_data.PatientDemographicsRace.White.label\",      \"value\": 74    },   "
            + " {      \"label\": \"charts.chart_data.PatientDemographicsRace.BlackOrAfricanAmerican.label\",      \"value\": 18    },    " 
            + " {      \"label\": \"charts.chart_data.PatientDemographicsRace.Asian.label\",      \"value\": 3    },    "
            + "{      \"label\": \"charts.chart_data.PatientDemographicsRace.Unknown.label\",      \"value\": 3    },    "
            + "{      \"label\": \"charts.chart_data.PatientDemographicsRace.NotReported.label\",      \"value\": 1    },   "
            + " {      \"label\": \"charts.chart_data.PatientDemographicsRace.NativeHawaiianOrOtherPacificIslander.label\",      \"value\": 1    }  ], "
            + " \"patientDemographicsEthnicity\": [    {      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.NotHispanicOrLatino.label\",      \"value\": 94    },    "
            + "{      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.HispanicOrLatino.label\",      \"value\": 4    },   "
            + " {      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.Unknown.label\",      \"value\": 1    },   "
            + " {      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.NotReported.label\",      \"value\": 1    }  ]}\n";

    JsonNode actualObj;
    public ChartDataController() {
    }

    @ApiOperation(value = "Method to return data for charts")
    @GetMapping(UrlConstants.URL_CHART_DATA) // publicapi/v1/chartData 
    public ResponseEntity<String> getAllChartData() throws JsonProcessingException {
    	
    	HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		List<ChartData> chartDataList = chartDataService.getChartData();
		List<AgeDemographicsData> ageDataList = chartDataService.getAgeDemographicsData();
		List<ProjectSummary> projectSummary = chartDataService.getProjectSummary();
 		// If there are no registered users, return back no content
//		if (CollectionUtils.isEmpty(chartDataList)) {
//			return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(httpHeaders)
//					.body(messageSource.getMessage(HttpResponseConstants.NO_SITE_FOUND_MSG, null, locale));
//		}
		
		List<ChartData> cancerTypeDataListJson = new ArrayList<>();
		List<ChartData> patientSexDataListJson = new ArrayList<>();
		List<ChartData> patientRaceDataListJson = new ArrayList<>();
		List<ChartData> patientEthnicityDataListJson = new ArrayList<>();
		List<ChartData> ruralUrbanDataListJson = new ArrayList<>();
		List<ChartData> bioDataListJson = new ArrayList<>();
		
		ObjectMapper mapper = new ObjectMapper();
		
		for(ChartData data : chartDataList ) {
			switch(data.getChart()){
			case CommonConstants.CHART_AREA :
				ruralUrbanDataListJson.add(data);
				break;
			case CommonConstants.CHART_BIOMARKER:
				bioDataListJson.add(data);
				break;
			case CommonConstants.CHART_CANCERTYPE:
				cancerTypeDataListJson.add(data);
				break;
			case CommonConstants.CHART_ETHNICITY:
				patientEthnicityDataListJson.add(data);
				break;
			case CommonConstants.CHART_RACE:
				patientRaceDataListJson.add(data);
				break;
			case CommonConstants.CHART_SEX:
				patientSexDataListJson.add(data);
				break;
			default:
					;
			}
		};
		
		ObjectNode responseNode = mapper.createObjectNode();
		
		responseNode.set("projectSummary", convertProjectSummaryDataToJSON(projectSummary));
		responseNode.set("cancerType", convertChartDataToJSON(cancerTypeDataListJson));
		responseNode.set("patientAge", convertAgeDataToJSON(ageDataList));
		responseNode.set("patientSex", convertChartDataToJSON(patientSexDataListJson));
		responseNode.set("ruralUrban", convertChartDataToJSON(ruralUrbanDataListJson));
		responseNode.set("patientRace", convertChartDataToJSON(patientRaceDataListJson));
		responseNode.set("patientEthnicity", convertChartDataToJSON(patientEthnicityDataListJson));
		responseNode.set("bioSpecimenParticipants", convertChartDataToJSON(bioDataListJson));
		
		return new ResponseEntity<String>( (mapper.writeValueAsString(responseNode)).replace("\\", "").replace("[\"", "[").replace("\"]", "]").replace("}\",\"{","},{"), 
				httpHeaders, HttpStatus.OK);
    }


        // build the return object here
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//           // actualObj = mapper.readTree(chartDataRepository.getChartData()); // @TODO finish chartDataRepository
//           actualObj = mapper.readTree(testData);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//       ;
//        return ResponseEntity.ok(actualObj);
		

		private ArrayNode convertChartDataToJSON(List<ChartData> chartDataList) throws JsonProcessingException {
			
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode arrayNode = mapper.createArrayNode();
			chartDataList.forEach(data -> {
				try {
					arrayNode.add(mapper.writeValueAsString(dozerBeanMapper.map(data,ChartDataDTO.class)));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			
			
			return arrayNode;
		}
		
		private ArrayNode convertProjectSummaryDataToJSON(List<ProjectSummary> projectSummaryData) throws JsonProcessingException {
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode arrayNode = mapper.createArrayNode();
			projectSummaryData.forEach(data -> {
				
				try {
					arrayNode.add(mapper.writeValueAsString(dozerBeanMapper.map(data,ProjectSummaryDTO.class)));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			
			return arrayNode;
		}
		
		private ArrayNode convertAgeDataToJSON(List<AgeDemographicsData> ageDataList) throws JsonProcessingException {
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode arrayNode = mapper.createArrayNode();
			ageDataList.forEach(data -> {
				try {
					arrayNode.add(mapper.writeValueAsString(dozerBeanMapper.map(data,AgeDemographicsDataDTO.class)));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			
			return arrayNode;
		}
		

    

}

