package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmotecaApplicationTest {

    private static final String TEST_PROFILE = "test";
    private String originalProfile;

    @BeforeEach
    void saveOriginalProfile() {
        originalProfile = System.getProperty("spring.profiles.active");
    }

    @AfterEach
    void restoreOriginalProfile() {
        if (originalProfile != null) {
            System.setProperty("spring.profiles.active", originalProfile);
        } else {
            System.clearProperty("spring.profiles.active");
        }
    }

    @Test
    void contextLoadsSuccessfully(ApplicationContext context) {
        assertNotNull(context, "Application context should not be null");
    }

    @Test
    void mainMethodStartsApplicationWithArguments() {
        assertDoesNotThrow(() -> FilmotecaApplication.main(new String[]{"--spring.profiles.active=" + TEST_PROFILE}),
                "Application should start with profile arguments");
    }

    @Test
    void applicationContextContainsRequiredBeans(ApplicationContext context) {
        assertAll(
                () -> assertNotNull(context.getBean("inMemoryCacheImpl"),
                        "inMemoryCacheImpl bean should be present"),
                () -> assertNotNull(context.getBean("labControllerAdvice"),
                        "labControllerAdvice bean should be present")
        );
    }

}