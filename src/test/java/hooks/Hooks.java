package hooks;

import filters.RequestResponseLoggingFilter;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class Hooks {
	
	public static RequestSpecification request;
    
    @Before
    public void setup() {
    	
    	request = RestAssured.given()
                .baseUri("https://userserviceapp-f5a54828541b.herokuapp.com")  
                .auth().basic("Numpy@gmail.com", "userapi@2025")   
                .contentType("application/json")
                .given().filter(new RequestResponseLoggingFilter()); 
    }
}
