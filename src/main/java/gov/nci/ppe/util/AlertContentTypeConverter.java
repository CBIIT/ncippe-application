package gov.nci.ppe.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import gov.nci.ppe.constants.CommonConstants.AlertContentType;

@Converter(autoApply = true)
public class AlertContentTypeConverter implements AttributeConverter<AlertContentType, String> {

	@Override
	public String convertToDatabaseColumn(AlertContentType contentType) {
		if (contentType == null) {
			return null;
		}
		return contentType.getContentType();
	}

	@Override
	public AlertContentType convertToEntityAttribute(String dbData) {
		return AlertContentType.getContentType(dbData);
	}

}
