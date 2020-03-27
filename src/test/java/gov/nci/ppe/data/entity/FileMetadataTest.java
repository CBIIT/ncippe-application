package gov.nci.ppe.data.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("unittest")
public class FileMetadataTest {

	private static final String UUID_PRESENT = "aaaa";
	private static final String UUID_ABSENT = "bbbb";

	private FileMetadata classUnderTest;

	@Before
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
