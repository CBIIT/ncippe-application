package gov.nci.ppe.data.dozer;

import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerConverter;

import gov.nci.ppe.data.entity.User;

public class UserToUserUUIDDozerConverter extends DozerConverter<User, String> {
	public UserToUserUUIDDozerConverter() {
		super(User.class, String.class);
	}

	@Override
	public String convertTo(User source, String destination) {
		return source.getUserUUID();
	}

	@Override
	public User convertFrom(String source, User destination) {
		if (StringUtils.isEmpty(source)) {
			return null;
		}
		if (destination == null) {
			destination = new User();
		}
		destination.setUserUUID(source);
		return destination;
	}

}
