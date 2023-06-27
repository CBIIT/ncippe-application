package gov.nci.ppe.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.nci.ppe.constants.UrlConstants;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.logging.Logger;

@RestController
public class ChartDataController {
    private Logger logger = Logger.getLogger(ChartDataController.class.getName());
    private    String testData  = "{\n" +
            "  \"patientDemographicsByCancerType\": [\n" +
            "    {\n" +
            "      \"label\": \"charts.chart_data.PatientDemographics.ColonCancer.label\",\n" +
            "      \"value\": 26\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"charts.chart_data.PatientDemographics.LungCancer.label\",\n" +
            "      \"value\": 21\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"charts.chart_data.PatientDemographics.ProstateCancer.label\",\n" +
            "      \"value\": 18\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"charts.chart_data.PatientDemographics.Melanoma.label\",\n" +
            "      \"value\": 16\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"charts.chart_data.PatientDemographics.GastroesophagealCancer.label\",\n" +
            "      \"value\": 9\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"charts.chart_data.PatientDemographics.MultipleMyeloma.label\",\n" +
            "      \"value\": 4\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"charts.chart_data.PatientDemographics.Leukemia.label\",\n" +
            "      \"value\": 6\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"charts.chart_data.PatientDemographics.AcuteMyeloid.label\",\n" +
            "      \"value\": 1\n" +
            "    }\n" +
            "  ],\n" +
            "  \"participantDemographicsAge\": [\n" +
            "    {\n" +
            "      \"label\": \"34-43\",\n" +
            "      \"value\": 8\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"44-53\",\n" +
            "      \"value\": 15\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"54-63\",\n" +
            "      \"value\": 23\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"64-73\",\n" +
            "      \"value\": 38\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"74-83\",\n" +
            "      \"value\": 15\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"84-93\",\n" +
            "      \"value\": 2\n" +
            "    }\n" +
            "  ],\n" +
            "  \"participantDemographicsSex\": [\n" +
            "    {\n" +
            "      \"label\": \"Male\",\n" +
            "      \"value\": 62\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"Female\",\n" +
            "      \"value\": 38\n" +
            "    }\n" +
            "  ],\n" +
            "  \"patientDemographicsRace\": [\n" +
            "    {\n" +
            "      \"label\": \"charts.chart_data.PatientDemographicsRace.White.label\",\n" +
            "      \"value\": 74\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"charts.chart_data.PatientDemographicsRace.BlackOrAfricanAmerican.label\",\n" +
            "      \"value\": 18\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"charts.chart_data.PatientDemographicsRace.Asian.label\",\n" +
            "      \"value\": 3\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"charts.chart_data.PatientDemographicsRace.Unknown.label\",\n" +
            "      \"value\": 3\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"charts.chart_data.PatientDemographicsRace.NotReported.label\",\n" +
            "      \"value\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"charts.chart_data.PatientDemographicsRace.NativeHawaiianOrOtherPacificIslander.label\",\n" +
            "      \"value\": 1\n" +
            "    }\n" +
            "  ],\n" +
            "  \"patientDemographicsEthnicity\": [\n" +
            "    {\n" +
            "      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.NotHispanicOrLatino.label\",\n" +
            "      \"value\": 94\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.HispanicOrLatino.label\",\n" +
            "      \"value\": 4\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.Unknown.label\",\n" +
            "      \"value\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.NotReported.label\",\n" +
            "      \"value\": 1\n" +
            "    }\n" +
            "  ]\n" +
            "}\n";

    public ChartDataController() {
        System.out.println("MHL IN ChartDataController constructor");
    }

    @ApiOperation(value = "Method to return data for charts")
    @GetMapping(UrlConstants.CHART_DATA)
    public ResponseEntity<String> isHere() {
        logger.info("MHL ChartDataController");
        return ResponseEntity.ok(testData);
    }


}
