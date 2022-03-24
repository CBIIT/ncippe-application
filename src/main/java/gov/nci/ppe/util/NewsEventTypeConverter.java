package gov.nci.ppe.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import gov.nci.ppe.constants.CommonConstants.NewsEventType;

@Converter(autoApply = true)
public class NewsEventTypeConverter implements AttributeConverter<NewsEventType, String> {

	@Override
	public String convertToDatabaseColumn(NewsEventType contentType) {
		if (contentType == null) {
			return null;
		}
		return contentType.getNewsEventType();
	}

	@Override
	public NewsEventType convertToEntityAttribute(String dbData) {

		return NewsEventType.getContentType(dbData);
	}

}
