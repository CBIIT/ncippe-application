package gov.nci.ppe.util;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class TimeUtil {

	public static Timestamp now() {
		ZoneId zone = ZoneId.of("America/New_York");
		return Timestamp.valueOf(OffsetDateTime.now(zone).toLocalDateTime());

	}
}
