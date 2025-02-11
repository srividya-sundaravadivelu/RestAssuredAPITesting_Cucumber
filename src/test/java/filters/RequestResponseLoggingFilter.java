package filters;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class RequestResponseLoggingFilter implements Filter {

	
	@Override
	public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec,
			FilterContext context) {

		Response response = context.next(requestSpec, responseSpec);
		
		System.out.print("Request URL: " + requestSpec.getBaseUri() + requestSpec.getUserDefinedPath());
		System.out.print("Request Body: " + requestSpec.getBody());
		
		// Skip logging response for "Get All Users" request - since the response is big
        if (requestSpec.getMethod().equalsIgnoreCase("GET") && requestSpec.getUserDefinedPath().contains("uap/users")) {
            return response;
        }
		
        System.out.print("Response: " + response.getBody().asPrettyString());
		return response;
	}

}

