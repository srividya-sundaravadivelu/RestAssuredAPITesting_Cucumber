package stepDefinitions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hooks.Hooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import models.User;
import models.UserAddress;
import utils.CSVTestDataFilter;
import org.testng.asserts.SoftAssert;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.notNullValue;

public class CreateNewUserSteps {

	private List<Map<String, String>> requestData;
	private List<User> users;
	private List<Response> responses;
	private List<Integer> expectedStatusCodes;
	private List<String> scenarioNames;

	@Given("user has request data from CSV")
	public void user_has_request_data_from_csv_for_request_name() {
		requestData = CSVTestDataFilter.getDataByRequestName("CreateNewUser");
	}

	@When("user sends a POST request to create a new user")
	public void user_sends_a_post_request_to_create_a_new_user() {
		responses = new ArrayList<>(); // Store multiple responses
		expectedStatusCodes = new ArrayList<>(); // Store multiple expected status codes
		users = new ArrayList<>();
		scenarioNames = new ArrayList<>();

		for (Map<String, String> data : requestData) {
			int expectedCode = Integer.parseInt(data.get("expected_status_code"));
			expectedStatusCodes.add(expectedCode);
			scenarioNames.add(data.get("Scenario"));

			// Create a user object with the data
			User user = new User();
			user.setUserContactNumber(data.get("user_contact_number"));
			user.setUserEmailId(data.get("user_email_id"));
			user.setUserFirstName(data.get("user_first_name"));
			user.setUserLastName(data.get("user_last_name"));

			UserAddress address = new UserAddress();
			address.setStreet(data.get("street"));
			address.setPlotNumber(data.get("plotNumber"));
			address.setState(data.get("state"));
			address.setCountry(data.get("country"));
			address.setZipCode(data.get("zipCode"));

			user.setUserAddress(address);

			// Store User Object
			users.add(user);

			Response response = Hooks.request.given().body(user).when().post(data.get("endpoint"));

			responses.add(response); // Store each response
		}
	}

	@Then("The response status code should match the expected value")
	public void the_response_status_code_should_match_the_expected_value() {

		SoftAssert softAssert = new SoftAssert();

		for (int i = 0; i < users.size(); i++) {
			User user = users.get(i);
			Response response = responses.get(i);
			int expectedCode = expectedStatusCodes.get(i);
			String scenario = scenarioNames.get(i);

			System.out.println("Validating post scenario " + scenario);

			// Status Code Assertion
			softAssert.assertEquals(response.getStatusCode(), expectedCode, "Status code mismatch:" + scenario);

			// Header Validation
			softAssert.assertEquals(response.getHeader("Content-Type"), "application/json", "Content-Type mismatch:" + scenario);

			if (response.getStatusCode() == 201)
			{
				// JSON Schema Validation
				response.then().assertThat().body(matchesJsonSchemaInClasspath("user_schema.json"));
	
				// Data Validation
				validateResponseData(response, user);
			}
		}

		// Report all failures at the end
		softAssert.assertAll();
	}

	private void validateResponseData(Response response, User user) {
		UserAddress address = user.getUserAddress();

		response.then().body("user_id", notNullValue())
				.body("creation_time", notNullValue())
				.body("last_mod_time", notNullValue())
				.body("user_first_name", equalTo(user.getUserFirstName()))
				.body("user_last_name", equalTo(user.getUserLastName()))
				.body("user_email_id", equalTo(user.getUserEmailId()))
				.body("user_contact_number", hasToString(user.getUserContactNumber()))
				.body("userAddress.plotNumber", equalTo(address.getPlotNumber()))
				.body("userAddress.street", equalTo(address.getStreet()))
				.body("userAddress.state", equalTo(address.getState()))
				.body("userAddress.country", equalTo(address.getCountry()))
				.body("userAddress.zipCode", equalTo(Integer.parseInt(address.getZipCode())))
				.body("userAddress.addressId", notNullValue());
	}

}
