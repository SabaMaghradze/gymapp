package com.gymapp.training.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainingSteps {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseEntity<String> latestResponse;
    private Map<String, Object> requestPayload;

    @Given("a valid training registration request")
    public void a_valid_training_registration_request() {
        requestPayload = Map.of(
                "traineeUsername", "trainee1",
                "trainerUsername", "trainer1",
                "trainingName", "Strength",
                "trainingDate", LocalDate.now().toString(),
                "duration", 60
        );
    }

    @Given("an invalid training registration request")
    public void an_invalid_training_registration_request() {
        requestPayload = Map.of(
                "trainerUsername", "trainer1",
                "trainingName", "Strength",
                "trainingDate", LocalDate.now().toString(),
                "duration", 60
        );
    }

    @When("I POST it to {string}")
    public void i_post_it_to(String path) throws Exception {
        latestResponse = restTemplate.postForEntity(
                path,
                requestPayload,
                String.class
        );
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer status) {
        assertThat(latestResponse.getStatusCodeValue()).isEqualTo(status);
    }
}