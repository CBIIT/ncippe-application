package gov.nci.ppe.data.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-12-16
 */
@Entity
@DiscriminatorValue("7")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class ContentEditor extends User {

}
