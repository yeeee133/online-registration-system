package com.example.registration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CI smoke test for GitHub Actions.
 *
 * This test intentionally avoids connecting to MySQL so that students can
 * observe a stable continuous testing pipeline first. API and database tests
 * can be added as advanced tasks after the CI pipeline is successful.
 */
class RegistrationApplicationTests {

    @Test
    void ciPipelineShouldRunJUnitSuccessfully() {
        assertTrue(true, "GitHub Actions should be able to run Maven/JUnit tests successfully.");
    }
}
