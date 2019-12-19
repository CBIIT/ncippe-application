package gov.nci.ppe.data.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-12-16
 */
@Entity
@DiscriminatorValue("7")
public class ContentEditor extends User {

}
