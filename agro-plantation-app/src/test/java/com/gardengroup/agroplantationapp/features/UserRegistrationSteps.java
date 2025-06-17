package com.gardengroup.agroplantationapp.features;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import com.gardengroup.agroplantationapp.model.dto.user.RegisterDTO;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class UserRegistrationSteps {

    @Autowired
    private WebTestClient webTestClient;

    ResponseSpec response;
    RegisterDTO registerDto;

    @Test

    @Given("the user wants to register with {string} and {string} and {string} and {string} and {string}")
    public void theUserWantsToRegisterWithAnd(String username, String password,
            String name, String lastname, String address) {

        registerDto = new RegisterDTO();
        registerDto.setEmail(username);
        registerDto.setPassword(password);
        registerDto.setName(name);
        registerDto.setLastname(lastname);
        registerDto.setAddress(address);

    }

    @When("the user submits the registration form")
    public void theUserSubmitsTheRegistrationForm() {
        response = webTestClient.post()
                .uri("/api/v1/users/register")
                .bodyValue(registerDto)
                .exchange();
    }

    @Then("the user should see a success message with http status 201")
    public void successMessageWithHttpStatus201() {
        response.expectStatus().isCreated();
    }

}
