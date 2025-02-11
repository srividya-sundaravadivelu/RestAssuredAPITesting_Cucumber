@userApi @createNewUser
Feature: Testing create new user in the User API

Scenario: Validate Create User API with CSV data
    Given user has request data from CSV
    When user sends a POST request to create a new user
    Then The response status code should match the expected value
