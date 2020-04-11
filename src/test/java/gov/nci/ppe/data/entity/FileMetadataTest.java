package gov.nci.ppe.data.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FileMetadataTest {

	private static final String UUID_PRESENT = "aaaa";
	private static final String UUID_ABSENT = "bbbb";

	private FileMetadata classUnderTest;

	@BeforeEach
	public void setUp() {
		classUnderTest = new FileMetadata();
		HashSet<User> viewBy = new HashSet<>();
		User user = new Participant();
		user.setUserUUID(UUID_PRESENT);
		user.setUserId(-1l);
		viewBy.add(user);
		classUnderTest.setViewedBy(viewBy);
	}

	@Test
	public void testHasViewed_Found() {
		assertTrue(classUnderTest.hasViewed(UUID_PRESENT));
	}

	@Test
	public void testHasViewed_Absent() {
		assertFalse(classUnderTest.hasViewed(UUID_ABSENT));

		classUnderTest.getViewedBy().clear();
		assertFalse(classUnderTest.hasViewed(UUID_PRESENT));
	}
}
