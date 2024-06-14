package gov.nci.ppe.data.entity;


import lombok.Data;

import java.time.LocalDateTime;

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
@Table(name = "ProjectSummaryView")
public class ProjectSummary {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "ParticipantsCount", nullable = false)
	private Long participantsCount;

	@Column(name = "SitesCount", nullable = false)
	private Long sitesCount;
	
	@Column(name = "CancerTypesCount", nullable = false)
	private Long cancerTypesCount;

	@Column(name = "BiomarkerReturnedCount")
	private Long biomarkerReturnedCount;

	@Column(name = "LastRevisedDate", nullable = false)
	private LocalDateTime lastRevisedDate;



}
