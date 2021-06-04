package gov.nci.ppe.data.dozer;

import com.github.dozermapper.core.DozerConverter;

import org.apache.commons.lang3.StringUtils;

import gov.nci.ppe.constants.PPERole;
import gov.nci.ppe.data.entity.Role;

/**
 * Converter to map from {@link PPERole} to {@link Role}
 * 
 * @author PublicisSapient
 * 
 * @version 2.3
 *
 * @since Mar 24, 2021
 *
 */

public class PPERoleToRoleDozerConverter extends DozerConverter<PPERole, Role> {
    public PPERoleToRoleDozerConverter() {
        super(PPERole.class, Role.class);
    }

    @Override
    public Role convertTo(PPERole source, Role destination) {
        if (source == null) {
            return null;
        }
        if (destination == null) {
            destination = new Role();
        }
        destination.setRoleName(source.name());
        return destination;
    }

    @Override
    public PPERole convertFrom(Role source, PPERole destination) {
        if (source == null || StringUtils.isBlank(source.getRoleName())) {
            return null;
        }
        return PPERole.valueOf(source.getRoleName());

    }
}
