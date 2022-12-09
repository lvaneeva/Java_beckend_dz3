import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.http.Headers;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;


public class ExampleTest extends AbstractTest {

    @Test
    @Disabled("Lesson")
    void getExampleTest() {
        given()
                .when()
                .get(getBaseUrl()+"recipes/716429/information?" +
                        "includeNutrition=false&apiKey=" +getApiKey())
                .then()
                .statusCode(200);

        given()
                .when()
                .request(Method.GET,getBaseUrl()+"recipes/716429/information?" +
                        "includeNutrition={Nutrition}&apiKey={apiKey}", false, getApiKey())
                .then()
                .statusCode(200);
    }
    @Test
    @Disabled("Lesson")
    void getSpecifyingRequestDataTest() {
        given()
                .queryParam("apiKey", getApiKey())
                .queryParam("includeNutrition", "false")
                .pathParam("id", 716429)
                .when()
                .get(getBaseUrl()+"recipes/{id}/information")
                .then()
                .statusCode(200);

        given()
                .when()
                .get(getBaseUrl()+"recipes/{id}/information?" +
                        "includeNutrition={Nutrition}&apiKey={apiKey}",716429, false, getApiKey())
                .then()
                .statusCode(200);

        given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title","Pork roast with green beans")
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .then()
                .statusCode(200);

        Cookie someCookie = new Cookie
                .Builder("some_cookie", "some_value")
                .setSecured(true)
                .setComment("some comment")
                .build();


        given().cookie("username","max")
                .cookie(someCookie)
                .when()
                .get(getBaseUrl()+"recipes/716429/information?" +
                        "includeNutrition=false&apiKey=" +getApiKey())
                .then()
                .statusCode(200);

        given().headers("username","max")
                .when()
                .get(getBaseUrl()+"recipes/716429/information?" +
                        "includeNutrition=false&apiKey=" +getApiKey())
                .then()
                .statusCode(200);

        given()
                .queryParam("hash", "a3da66460bfb7e62ea1c96cfa0b7a634a346ccbf")
                .queryParam("apiKey", getApiKey())
                .body("{\n"
                        + " \"date\": 1644881179,\n"
                        + " \"slot\": 1,\n"
                        + " \"position\": 0,\n"
                        + " \"type\": \"INGREDIENTS\",\n"
                        + " \"value\": {\n"
                        + " \"ingredients\": [\n"
                        + " {\n"
                        + " \"name\": \"1 banana\"\n"
                        + " }\n"
                        + " ]\n"
                        + " }\n"
                        + "}")
                .when()
                .post(getBaseUrl()+"mealplanner/geekbrains/items")
                .then()
                .statusCode(200);

    }
    @Test
    @Disabled("Lesson")
    void getResponseData(){
        Response response = given()
                .when()
                .get(getBaseUrl()+"recipes/716429/information?" +
                        "includeNutrition=false&apiKey=" +getApiKey());

        // Get all headers
        Headers allHeaders = response.getHeaders();
        // Get a single header value:
        System.out.println("Content-Encoding: " + response.getHeader("Content-Encoding"));

        // Get all cookies as simple name-value pairs
        Map<String, String> allCookies = response.getCookies();
        // Get a single cookie value:
        System.out.println("CookieName: " + response.getCookie("cookieName"));

        // Get status line
        System.out.println("StatusLine: " + response.getStatusLine());
        // Get status code
        System.out.println("Code: " + response.getStatusCode());


        String cuisine = given()
                .queryParam("apiKey", getApiKey())
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .path("cuisine");

        System.out.println("cuisine: " + cuisine);

        response = given()
                .queryParam("apiKey", getApiKey())
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .then().extract().response();

        String confidence = given()
                .queryParam("apiKey", getApiKey())
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .then().extract()
                .jsonPath()
                .get("confidence")
                .toString();

        System.out.println("confidence: " + confidence);

    }

    @Test
    @Disabled("Lesson")
    void getVerifyingResponseData(){

        JsonPath response = given()
                .queryParam("apiKey", getApiKey())
                .queryParam("includeNutrition", "false")
                .when()
                .get("https://api.spoonacular.com/recipes/716429/information")
                .body()
                .jsonPath();
        assertThat(response.get("vegetarian"), is(false));
        assertThat(response.get("vegan"), is(false));
        assertThat(response.get("license"), equalTo("CC BY-SA 3.0"));
        assertThat(response.get("pricePerServing"), equalTo(163.15F));
        assertThat(response.get("extendedIngredients[0].aisle"), equalTo("Milk, Eggs, Other Dairy"));


        given()
                .queryParam("apiKey", getApiKey())
                .queryParam("includeNutrition", "false")
                .when()
                .get("https://api.spoonacular.com/recipes/716429/information")
                .then()
                .assertThat()
                //.cookie("cookieName", "cookieValue")
                .statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .statusLine(containsString("OK"))
                .header("Connection", "keep-alive")
                .header("Content-Length", Integer::parseInt, lessThan(3000))
                .contentType(ContentType.JSON)
                //  .body(equalTo("something"))
                .time(lessThan(2000L));

        given()
                .queryParam("apiKey", getApiKey())
                .queryParam("includeNutrition", "false")
                .response()
                .contentType(ContentType.JSON)
                .time(lessThan(2000L))
                .header("Connection", "keep-alive")
                .expect()
                .body("vegetarian", is(false))
                .body("vegan", is(false))
                .body("license", equalTo("CC BY-SA 3.0"))
                .body("pricePerServing", equalTo(163.15F))
                .body("extendedIngredients[0].aisle", equalTo("Milk, Eggs, Other Dairy"))
                .when()
                .get("https://api.spoonacular.com/recipes/716429/information");

    }
    @Test
    @Disabled("Lesson")
    void addMealTest() {
        String id = given()
                .queryParam("hash", "a3da66460bfb7e62ea1c96cfa0b7a634a346ccbf")
                .queryParam("apiKey", getApiKey())
                .body("{\n"
                        + " \"date\": 1644881179,\n"
                        + " \"slot\": 1,\n"
                        + " \"position\": 0,\n"
                        + " \"type\": \"INGREDIENTS\",\n"
                        + " \"value\": {\n"
                        + " \"ingredients\": [\n"
                        + " {\n"
                        + " \"name\": \"1 banana\"\n"
                        + " }\n"
                        + " ]\n"
                        + " }\n"
                        + "}")
                .when()
                .post("https://api.spoonacular.com/mealplanner/geekbrains/items")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("id")
                .toString();

        given()
                .queryParam("hash", "a3da66460bfb7e62ea1c96cfa0b7a634a346ccbf")
                .queryParam("apiKey", getApiKey())
                .delete("https://api.spoonacular.com/mealplanner/geekbrains/items/" + id)
                .then()
                .statusCode(200);
    }


    @Test
    void getRecipeByCuisinePositiveTest() {
        given()
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch?" +
                        "cuisine=american&apiKey=bc68ed3029d34fa285a7f61a03e60aab")
                .then()
                .statusCode(200);
    }
    @Test
    void getRecipeByQueryPositiveTest() {
        given()
                .when()
                .get(getBaseUrl()+"recipes/complexSearch?" +
                        "query=pasta&apiKey=" +getApiKey())
                .then()
                .statusCode(200);
    }
    @Test
    void getRecipeByDietPositiveTest() {
        given()
                .when()
                .request(Method.GET, getBaseUrl() + "recipes/complexSearch?" +
                        "diet={diet}&apiKey={apiKey}", "vegetarian", getApiKey())
                .then()
                .statusCode(200);
    }
    @Test
    void getRecipeByIntolerancesPositiveTest() {
        given()
                .queryParam("apiKey", getApiKey())
                .queryParam("intolerances", "soy,egg")
                .when()
                .get(getBaseUrl()+"recipes/complexSearch")
                .then()
                .statusCode(200);
    }
    @Test
    void getRecipeByIncludeIngredients() {
        given()
                .queryParam("apiKey", getApiKey())
                .queryParam("includeIngredients", "mushroom")
                .when()
                .get(getBaseUrl()+"recipes/complexSearch")
                .then()
                .statusCode(200);
    }

    @Test
    void postRecipeByTitle(){
        given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title","schnitzel")
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .then()
                .statusCode(200);
    }
    @Test
    void postRecipeByTitleNegativeTest(){
        given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title","шницель")
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .then()
                .statusCode(200);
    }
    @Test
    void postRecipeByTitleLang(){
        given()
                .queryParam("apiKey", getApiKey())
                .queryParam("language", "en")
                .contentType("application/x-www-form-urlencoded")
                .formParam("title","German Sauerbraten Stew")
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .then()
                .statusCode(200);
    }
    @Test
    void postRecipeByTitleLangDe(){
        given()
                .queryParam("apiKey", getApiKey())
                .queryParam("language", "de")
                .contentType("application/x-www-form-urlencoded")
                .formParam("title","German Sauerbraten Stew")
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .then()
                .statusCode(200);
    }
    @Test
    void postRecipeByTitleIngredientList(){
        given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title","burger")
                .formParam("ingredientList","black pepper")
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .then()
                .statusCode(200);
    }

    @Test
    void shoppingListChain() {
        given()
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
                .post(getBaseUrl() + "users/connect")
                .then()
                .statusCode(200);

        String cost=given()
                .queryParam("apiKey", getApiKey())
                .queryParam("hash", "721852823033a011976356aad7dab312ba32a118")
                .pathParam("username", "francesco-franecki51")
                .pathParam("start-date", "2022-12-07")
                .pathParam("end-date", "2022-12-13")
                .when()
                .post(getBaseUrl()+"mealplanner/{username}/shopping-list/{start-date}/{end-date}")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("cost")
                .toString();
        System.out.println(cost);

        String id = given()
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
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("id")
                .toString();
        System.out.println(id);
        given()
                .queryParam("apiKey", getApiKey())
                .queryParam("hash", "721852823033a011976356aad7dab312ba32a118")
                .pathParam("username", "francesco-franecki51")
                .when()
                .get(getBaseUrl()+"mealplanner/{username}/shopping-list")
                .then()
                .statusCode(200);


        given()
                .queryParam("hash", "721852823033a011976356aad7dab312ba32a118")
                .queryParam("apiKey", getApiKey())
                .pathParam("username", "francesco-franecki51")
                .pathParam("id", id)
                .delete(getBaseUrl()+"mealplanner/{username}/shopping-list/items/{id}")
                .then()
                .statusCode(200);
    }

}
