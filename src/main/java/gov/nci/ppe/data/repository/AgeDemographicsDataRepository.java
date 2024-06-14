package gov.nci.ppe.data.repository;

import gov.nci.ppe.data.entity.AgeDemographicsData;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


/**
 * @author Chao Zhang
 * @version 2.8
 * @since June 2024
 */

@Repository
public interface AgeDemographicsDataRepository  extends JpaRepository<AgeDemographicsData, Long> {


}
