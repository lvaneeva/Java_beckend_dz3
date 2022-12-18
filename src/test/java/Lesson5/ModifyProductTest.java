package Lesson5;

import Lesson5.api.ProductService;
import Lesson5.dto.Product;
import Lesson5.utils.RetrofitUtils;
import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ModifyProductTest {
    static ProductService productService;
   // Call<Product> product = null;
   Product product;
    Faker faker = new Faker();
    int id;

    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit()
                .create(ProductService.class);
    }
    @SneakyThrows
    @BeforeEach
    void setUp() {
       product =  productService.getProductById(6)
               .execute().body()
               .withTitle("Bread")
               .withPrice(75)
               .withCategoryTitle("Food");
    }

    @Test
    void modifyProductInFoodCategoryTest() throws IOException {

        Response<Product> response  = productService.modifyProduct(product)
                .execute();
      //  id =  response.body().getId();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getPrice(),equalTo(75));
    }

}
