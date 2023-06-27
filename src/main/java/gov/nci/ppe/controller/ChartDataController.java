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
    private    String testData  = new StringBuilder().
            append("{\n").
            append("  \"patientDemographicsByCancerType\": [\n").
            append("    {\n").
            append("      \"label\": \"charts.chart_data.PatientDemographics.ColonCancer.label\",\n").
            append("      \"value\": 26\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"charts.chart_data.PatientDemographics.LungCancer.label\",\n").
            append("      \"value\": 21\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"charts.chart_data.PatientDemographics.ProstateCancer.label\",\n").
            append("      \"value\": 18\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"charts.chart_data.PatientDemographics.Melanoma.label\",\n").
            append("      \"value\": 16\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"charts.chart_data.PatientDemographics.GastroesophagealCancer.label\",\n").
            append("      \"value\": 9\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"charts.chart_data.PatientDemographics.MultipleMyeloma.label\",\n").
            append("      \"value\": 4\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"charts.chart_data.PatientDemographics.Leukemia.label\",\n").
            append("      \"value\": 6\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"charts.chart_data.PatientDemographics.AcuteMyeloid.label\",\n").
            append("      \"value\": 1\n").
            append("    }\n").
            append("  ],\n").
            append("  \"participantDemographicsAge\": [\n").
            append("    {\n").
            append("      \"label\": \"34-43\",\n").
            append("      \"value\": 8\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"44-53\",\n").
            append("      \"value\": 15\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"54-63\",\n").
            append("      \"value\": 23\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"64-73\",\n").
            append("      \"value\": 38\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"74-83\",\n").
            append("      \"value\": 15\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"84-93\",\n").
            append("      \"value\": 2\n").
            append("    }\n").
            append("  ],\n").
            append("  \"participantDemographicsSex\": [\n").
            append("    {\n").
            append("      \"label\": \"Male\",\n").
            append("      \"value\": 62\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"Female\",\n").
            append("      \"value\": 38\n").
            append("    }\n").
            append("  ],\n").
            append("  \"patientDemographicsRace\": [\n").
            append("    {\n").
            append("      \"label\": \"charts.chart_data.PatientDemographicsRace.White.label\",\n").
            append("      \"value\": 74\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"charts.chart_data.PatientDemographicsRace.BlackOrAfricanAmerican.label\",\n").
            append("      \"value\": 18\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"charts.chart_data.PatientDemographicsRace.Asian.label\",\n").
            append("      \"value\": 3\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"charts.chart_data.PatientDemographicsRace.Unknown.label\",\n").
            append("      \"value\": 3\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"charts.chart_data.PatientDemographicsRace.NotReported.label\",\n").
            append("      \"value\": 1\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"charts.chart_data.PatientDemographicsRace.NativeHawaiianOrOtherPacificIslander.label\",\n").
            append("      \"value\": 1\n").
            append("    }\n").
            append("  ],\n").
            append("  \"patientDemographicsEthnicity\": [\n").
            append("    {\n").
            append("      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.NotHispanicOrLatino.label\",\n").
            append("      \"value\": 94\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.HispanicOrLatino.label\",\n").
            append("      \"value\": 4\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.Unknown.label\",\n").
            append("      \"value\": 1\n").
            append("    },\n").
            append("    {\n").
            append("      \"label\": \"charts.chart_data.PatientDemographicsEthnicity.NotReported.label\",\n").
            append("      \"value\": 1\n").
            append("    }\n").
            append("  ]\n").
            append("}\n").toString();

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

