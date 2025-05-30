package com.healthyForum;

import com.healthyForum.model.User;
import com.healthyForum.repository.UserRepository;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = HealthyForumApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

public class SleepTest {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        // Clean and insert a user (optional if using data.sql)
        // userRepository.deleteAll();

        //        testUser = new User(
        //                null,
        //                "sarah1",
        //                passwordEncoder.encode("abc123"),
        //                "Sarah Smith",
        //                "Female",
        //                "sarah@example.com",
        //                LocalDate.of(1990, 1, 1),
        //                "123 Wellness St."
        //        );
        //
        //        userRepository.save(testUser);
    }

    @Test
    void shouldCreateANewSleepEntry() {
        SleepEntry newSleepEntry = new SleepEntry(
                null,
                LocalDate.of(2025, 5, 26),
                LocalTime.of(22, 0),
                LocalTime.of(6, 30),
                8,
                "Slept well",
                testUser
        );

        ResponseEntity<Void> createResponse = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .postForEntity("/sleep", newSleepEntry, Void.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void shouldReturnAPageOfSleepList() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1", "abc123").getForEntity("/sleep/list-json?page=0&size=4", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(4);
    }

}
