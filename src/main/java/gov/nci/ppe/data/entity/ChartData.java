package gov.nci.ppe.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import gov.nci.ppe.constants.CommonConstants.AlertContentType;
import lombok.Data;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

@Data
@Entity
@Table(name = "ChartDataView")
public class ChartData {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "chart", nullable = false)
	private String chart;

	@Column(name = "Type", nullable = false)
	private String dataType;

	@Column(name = "Count")
	private Long dataValue;

	@Column(name = "Label", nullable = false)
	private String dataLabel;

	public String getChart() {
		return this.chart;
	}

}
