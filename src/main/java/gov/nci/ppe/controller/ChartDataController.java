package gov.nci.ppe.controller;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nci.ppe.constants.UrlConstants;
import gov.nci.ppe.data.repository.ChartDataRepository;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@RestController
public class ChartDataController {
    private Logger logger = Logger.getLogger(ChartDataController.class.getName());

    @Autowired
    private ChartDataRepository chartDataRepository;

    @JsonValue
    String testData  = "{" +
            " \"projectSummary\": [ { \"label\": \"charts.chart_data.ProjectSummary.ParticipantsEnrolled\", \"value\": 27}, { \"label\": \"charts.chart_data.ProjectSummary.SitesThatHaveEnrolledParticipants\", \"value\": 21}, { \"label\": \"charts.chart_data.ProjectSummary.CancerTypes\", \"value\": 18}, { \"label\": \"charts.chart_data.ProjectSummary.BiomarkerTestReturned\", \"value\": 16}], " +
            "  \"patientDemographicsByCancerType\": [    {      \"label\": \"charts.chart_data.PatientDemographics.ColonCancer.label\",      \"value\": 26    },    {      \"label\": \"charts.chart_data.PatientDemographics.LungCancer.label\",      \"value\": 21    },    {      \"label\": \"charts.chart_data.PatientDemographics.ProstateCancer.label\",      \"value\": 18    },    {      \"label\": \"charts.chart_data.PatientDemographics.Melanoma.label\",      \"value\": 16    },    {      \"label\": \"charts.chart_data.PatientDemographics.GastroesophagealCancer.label\",      \"value\": 9    },    {      \"label\": \"charts.chart_data.PatientDemographics.MultipleMyeloma.label\",      \"value\": 4    },    {      \"label\": \"charts.chart_data.PatientDemographics.Leukemia.label\",      \"value\": 6    },    {      \"label\": \"charts.chart_data.PatientDemographics.AcuteMyeloid.label\",      \"value\": 1    }  ],  \"participantDemographicsAge\": [    {      \"label\": \"34-43\",      \"value\": 38    },    {      \"label\": \"44-53\",      \"value\": 15    },    {      \"label\": \"54-63\",      \"value\": 23    },    {      \"label\": \"64-73\",      \"value\": 8    },    {      \"label\": \"74-83\",      \"value\": 15    },    {      \"label\": \"84-93\",      \"value\": 2    }  ],  \"participantDemographicsSex\": [    {      \"label\": \"Male\",      \"value\": 62    },    {      \"label\": \"Female\",      \"value\": 38    }  ],  \"patientDemographicsRace\": [    {      \"label\": \"charts.chart_data.PatientDemographicsRace.White.label\",      \"value\": 74    },    {      \"label\": \"charts.chart_data.PatientDemographicsRace.BlackOrAfricanAmerican.label\",      \"value\": 18    },    {      \"label\": \"charts.chart_data.PatientDemographicsRace.Asian.label\",      \"value\": 3    },    {      \"label\": \"charts.chart_data.PatientDemographicsRace.Unknown.label\",      \"value\": 3    },    {      \"label\": \"charts.chart_data.PatientDemographicsRace.NotReported.label\",      \"value\": 1    },    {      \"label\": \"charts.chart_data.PatientDemographicsRace.NativeHawaiianOrOtherPacificIslander.label\",      \"value\": 1    }  ],  \"patientDemographicsEthnicity\": [    {      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.NotHispanicOrLatino.label\",      \"value\": 94    },    {      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.HispanicOrLatino.label\",      \"value\": 4    },    {      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.Unknown.label\",      \"value\": 1    },    {      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.NotReported.label\",      \"value\": 1    }  ]}\n";

    JsonNode actualObj;
    public ChartDataController() {
        System.out.println("MHL IN ChartDataController constructor");
    }

    @ApiOperation(value = "Method to return data for charts")
    @GetMapping(UrlConstants.CHART_DATA)
    public ResponseEntity<JsonNode> getAllChartData() {

        // build the return object here
        ObjectMapper mapper = new ObjectMapper();
        try {
            if( chartDataRepository != null ){
                System.out.println("MHL IN ChartDataController chartDataRepository != null");
                System.out.println("MHL chartDataRepository: " +chartDataRepository );
            }
           // actualObj = mapper.readTree(chartDataRepository.getChartData()); //
             actualObj = mapper.readTree(testData); // @TODO Test data
        } catch (IOException e) {
            e.printStackTrace();
        }
       ;
        return ResponseEntity.ok(actualObj);

    }

}

