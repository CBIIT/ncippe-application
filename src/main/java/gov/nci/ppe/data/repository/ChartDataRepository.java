package gov.nci.ppe.data.repository;

import gov.nci.ppe.data.entity.ChartData;
import gov.nci.ppe.data.entity.PortalNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChartDataRepository  extends JpaRepository<PortalNotification, Long> {

     String sqlString = "select distinct case \n" +
            "\twhen pd.MedDRADiseaseTerm = 'Colorectal Carcinoma' Then 'Colon Cancer'\n" +
            "    when pd.MedDRADiseaseTerm = 'Non-Small Cell Lung Carcinoma' Then 'Lung Cancer'\n" +
            "    when pd.MedDRADiseaseTerm = 'Small Cell Lung Carcinoma' Then 'Lung Cancer'\n" +
            "    when pd.MedDRADiseaseTerm = 'Colon Adenocarcinoma' Then 'Colon Cancer'\n" +
            "    when pd.MedDRADiseaseTerm = 'Plasma Cell Myeloma' then 'Multiple Myeloma'\n" +
            "    when pd.MedDRADiseaseTerm = 'Acute Myeloid Leukemia Not Otherwise Specified' then 'Acute Myeloid Leukemia'\n" +
            "    when pd.MedDRADiseaseTerm = 'Prostate Carcinoma' then 'Prostate Cancer'\n" +
            "    when pd.MedDRADiseaseTerm = 'Adenocarcinoma of the Gastroesophageal Junction' then 'Gastroesophageal Cancer'\n" +
            "    else pd.MedDRADiseaseTerm \n" +
            "End As 'CancerType' ,\n" +
            "count(case \n" +
            "\twhen pd.MedDRADiseaseTerm = 'Colorectal Carcinoma' Then 'Colon Cancer'\n" +
            "    when pd.MedDRADiseaseTerm = 'Non-Small Cell Lung Carcinoma' Then 'Lung Cancer'\n" +
            "    when pd.MedDRADiseaseTerm = 'Small Cell Lung Carcinoma' Then 'Lung Cancer'\n" +
            "    when pd.MedDRADiseaseTerm = 'Colon Adenocarcinoma' Then 'Colon Cancer'\n" +
            "    when pd.MedDRADiseaseTerm = 'Plasma Cell Myeloma' then 'Multiple Myeloma'\n" +
            "    when pd.MedDRADiseaseTerm = 'Acute Myeloid Leukemia Not Otherwise Specified' then 'Acute Myeloid Leukemia'\n" +
            "    when pd.MedDRADiseaseTerm = 'Prostate Carcinoma' then 'Prostate Cancer'\n" +
            "    when pd.MedDRADiseaseTerm = 'Adenocarcinoma of the Gastroesophageal Junction' then 'Gastroesophageal Cancer'\n" +
            "    else pd.MedDRADiseaseTerm \n" +
            "End) As 'Count'\n" +
            "from ParticipantData pd\n" +
            "group by CancerType\n" +
            "order by CancerType;\n";
    @Query(value = sqlString,nativeQuery=true)
    String getChartData();

}
