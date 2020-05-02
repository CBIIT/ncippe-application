package gov.nci.ppe;

import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInfo;
import org.mockito.MockitoAnnotations;

@Tag("unittest")
public interface BaseMockitoTest {

	static final Logger logger = Logger.getLogger(BaseMockitoTest.class.getName());

	@BeforeEach
	default void beforeEachTest(TestInfo testInfo) {
		logger.info(() -> String.format("About to execute [%s]", testInfo.getDisplayName()));
		MockitoAnnotations.initMocks(this);
	}
}
