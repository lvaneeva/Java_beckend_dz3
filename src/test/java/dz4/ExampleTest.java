package dz4;


import Lesson4.Response;
import Lesson4.UsersConnectResponse;
import io.restassured.http.Method;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExampleTest extends AbstractTest {

    @Test
    @Disabled
    void getRecipePositiveTest() {
        given().spec(getRequestSpecification())
                .when()
                .get("https://api.spoonacular.com/recipes/716429/information")
                .then()
                .spec(responseSpecification);
    }


    @Test
    @Disabled
    void getAccountInfoWithExternalEndpointTest(){
        Response response = given().spec(requestSpecification)
                .when()
                .formParam("title","Burger")
                .post("https://api.spoonacular.com/recipes/cuisine").prettyPeek()
                .then()
                .extract()
                .response()
                .body()
                .as(Response.class);
        assertThat(response.getCuisine(), containsString("American"));
    }

    @Test
    @Disabled
    void test(){
        given().spec(requestSpecification)
                .when()
                .formParam("title","Burger")
                .formParam("language", "en")
                .post("https://api.spoonacular.com/recipes/cuisine").prettyPeek()
                .then()
                .statusCode(200);
    }


    ///////////////////////////////////////////////
    @Test
    void getRecipeByCuisinePositiveTest() {
        given().spec(getRequestSpecification())
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch?" +
                        "cuisine=american&apiKey=bc68ed3029d34fa285a7f61a03e60aab")
                .then()
                .spec(responseSpecification);
    }
    @Test
    void getRecipeByQueryPositiveTest() {
        given().spec(requestSpecification)
                .when()
                .get(getBaseUrl()+"recipes/complexSearch?" +
                        "query=pasta&apiKey=" +getApiKey())
                .then()
                .spec(responseSpecification);


    }
    @Test
    void getRecipeByDietPositiveTest() {
        given().spec(requestSpecification)
                .when()
                .request(Method.GET, getBaseUrl() + "recipes/complexSearch?" +
                        "diet={diet}&apiKey={apiKey}", "vegetarian", getApiKey())
                .then()
                .spec(responseSpecification);
    }
    @Test
    void getRecipeByIntolerancesPositiveTest() {
        given().spec(requestSpecification)
                .queryParam("apiKey", getApiKey())
                .queryParam("intolerances", "soy,egg")
                .when()
                .get(getBaseUrl()+"recipes/complexSearch")
                .then()
                .spec(responseSpecification);
    }
    @Test
    void getRecipeByIncludeIngredients() {
        given().spec(requestSpecification)
                .queryParam("apiKey", getApiKey())
                .queryParam("includeIngredients", "mushroom")
                .when()
                .get(getBaseUrl()+"recipes/complexSearch")
                .then()
                .spec(responseSpecification);
    }

    @Test
    void postRecipeByTitle(){
        given().spec(requestSpecification)
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title","schnitzel")
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .then()
                .spec(responseSpecification);
    }
    @Test
    void postRecipeByTitleNegativeTest(){
        given().spec(requestSpecification)
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title","шницель")
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .then()
                .spec(responseSpecification);
    }
    @Test
    void postRecipeByTitleLang(){
        given().spec(requestSpecification)
                .queryParam("apiKey", getApiKey())
                .queryParam("language", "en")
                .contentType("application/x-www-form-urlencoded")
                .formParam("title","German Sauerbraten Stew")
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .then()
                .spec(responseSpecification);
    }
    @Test
    void postRecipeByTitleLangDe(){
        given().spec(requestSpecification)
                .queryParam("apiKey", getApiKey())
                .queryParam("language", "de")
                .contentType("application/x-www-form-urlencoded")
                .formParam("title","German Sauerbraten Stew")
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .then()
                .spec(responseSpecification);
    }
    @Test
    void postRecipeByTitleIngredientList(){
        given().spec(requestSpecification)
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title","burger")
                .formParam("ingredientList","black pepper")
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .then()
                .spec(responseSpecification);
    }

    @Test
    void shoppingListChain() {
        UsersConnectResponse usersConnectResponse = given().spec(requestSpecification)
                .queryParam("apiKey", getApiKey())
                .queryParam("hash", "721852823033a011976356aad7dab312ba32a118")
                .body("{\n"
                        + " \"username\": \"francesco-franecki51\",\n"
                        + " \"firstname\": \"francesco\",\n"
                        + " \"lastname\": \"franecki\",\n"
                        + " \"email\": \"larisa_sap@inbox.ru\",\n"
                        + " \"spoonacularPassword\": \"sportsdrinkon53peashoots\"\n"
                        + "}")
                .when()
                .post(getBaseUrl() + "users/connect").prettyPeek()

                .then()
                .extract()
                .response()
                .body()
                .as(UsersConnectResponse.class);
        assertThat(usersConnectResponse.getUsername(), containsString("francesco"));

        String cost=given().spec(requestSpecification)
                .queryParam("apiKey", getApiKey())
                .queryParam("hash", "721852823033a011976356aad7dab312ba32a118")
                .pathParam("username", "francesco-franecki51")
                .pathParam("start-date", "2022-12-07")
                .pathParam("end-date", "2022-12-13")
                .when()
                .post(getBaseUrl()+"mealplanner/{username}/shopping-list/{start-date}/{end-date}")
                .then()
                .spec(responseSpecification)
                .extract()
                .jsonPath()
                .get("cost")
                .toString();
     //   System.out.println(cost);

        String id = given().spec(requestSpecification)
                .queryParam("apiKey", getApiKey())
                .queryParam("hash", "721852823033a011976356aad7dab312ba32a118")
                .pathParam("username", "francesco-franecki51")
                .body("{\n"
                        + " \"item\": \"1 package baking powder\",\n"
                        + " \"aisle\": \"Baking\",\n"
                        + " \"parse\": 1\n"
                        + "}")
                .when()
                .post(getBaseUrl()+"mealplanner/{username}/shopping-list/items")
                .then()
                .spec(responseSpecification)
                .extract()
                .jsonPath()
                .get("id")
                .toString();
       // System.out.println(id);
        given().spec(requestSpecification)
                .queryParam("apiKey", getApiKey())
                .queryParam("hash", "721852823033a011976356aad7dab312ba32a118")
                .pathParam("username", "francesco-franecki51")
                .when()
                .get(getBaseUrl()+"mealplanner/{username}/shopping-list")
                .then()
                .spec(responseSpecification);


        given().spec(requestSpecification)
                .queryParam("hash", "721852823033a011976356aad7dab312ba32a118")
                .queryParam("apiKey", getApiKey())
                .pathParam("username", "francesco-franecki51")
                .pathParam("id", id)
                .delete(getBaseUrl()+"mealplanner/{username}/shopping-list/items/{id}")
                .then()
                .spec(responseSpecification);
    }
}
