package gov.nci.ppe.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import gov.nci.ppe.constants.CommonConstants.LanguageOption;

@Converter(autoApply = true)
public class LanguageOptionConverter implements AttributeConverter<LanguageOption, String> {

	@Override
	public String convertToDatabaseColumn(LanguageOption lang) {
		if (lang == null) {
			return null;
		}
		return lang.getLanguage();
	}

	@Override
	public LanguageOption convertToEntityAttribute(String dbData) {
		return LanguageOption.getLanguageOption(dbData);
	}

}
