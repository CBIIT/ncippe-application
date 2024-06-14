package gov.nci.ppe.data.entity;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Chao Zhang
 * @version 2.8
 * @since June 2024
 */

@Data
@Entity
@Table(name = "AgeDemographicsView")
public class AgeDemographicsData {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "AgeGroup", nullable = false)
	private String ageGroup;

	@Column(name = "Count")
	private Long count;

}
