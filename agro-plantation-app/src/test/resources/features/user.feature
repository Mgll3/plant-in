Feature: Register and Login

    Scenario: User Registration Valid
        Given the user wants to register with "test@gmail.com" and "password123" and "Miguel" and "Alvarez" and "Calle 123"
        When the user submits the registration form
        Then the user should see a success message with http status 201

