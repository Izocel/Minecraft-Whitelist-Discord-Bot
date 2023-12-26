import java.util.logging.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

class StandardTests {
    public final Logger logger = Logger.getLogger(this.getClass().getSimpleName());


    @BeforeAll
    void beforeAllTests() {
        logger.info("Before all tests");
    }

    @AfterAll
    void afterAllTests() {
        logger.info("After all tests");
    }

    @BeforeEach
    void beforeEachTest(TestInfo testInfo) {
        logger.info(() -> String.format("Executing [%s]",
                testInfo.getDisplayName()));
    }

    @AfterEach
    void afterEachTest(TestInfo testInfo) {
        logger.info(() -> String.format("Finished executing [%s]",
                testInfo.getDisplayName()));
    }

}