package gov.nci.ppe.data.dozer;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.github.dozermapper.core.DozerConverter;

public class LocalDateTimeToTimestampDozerConverter extends DozerConverter<LocalDateTime, Timestamp> {

	public LocalDateTimeToTimestampDozerConverter() {
		super(LocalDateTime.class, Timestamp.class);
	}

	@Override
	public Timestamp convertTo(LocalDateTime source, Timestamp destination) {
		if (source == null) {
			return null;
		}
		return Timestamp.valueOf(source);
	}

	@Override
	public LocalDateTime convertFrom(Timestamp source, LocalDateTime destination) {
		if (source == null) {
			return null;
		}
		return source.toLocalDateTime();
	}

}
