package lesson6;

import Lesson5.api.ProductService;
import Lesson5.dto.Product;
import Lesson5.utils.RetrofitUtils;
import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
       product =  productService.getProductById(2)
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


        SqlSession session = null;
        Integer afterCount;
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sqlSessionFactory = new
                    SqlSessionFactoryBuilder().build(inputStream);
            session = sqlSessionFactory.openSession();
            db.dao.ProductsMapper productsMapper = session.getMapper(db.dao.ProductsMapper.class);

            db.model.ProductsExample example = new db.model.ProductsExample();
            example.createCriteria().andTitleEqualTo("Bread");
            List<db.model.Products> list = productsMapper.selectByExample(example);

            afterCount= list.get(0).getPrice();
            session.commit();
        }
        finally {
            session.close();
        }
        assertEquals(afterCount, response.body().getPrice());
    }

}
