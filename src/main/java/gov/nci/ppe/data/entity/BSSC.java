package gov.nci.ppe.data.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-01
 */
@Entity
@DiscriminatorValue("5")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class BSSC extends User {

}
