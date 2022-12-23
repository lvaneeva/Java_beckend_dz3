package lesson6;

import Lesson5.api.ProductService;
import Lesson5.utils.RetrofitUtils;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReturnProductsTest {
    static ProductService productService;
static String afterTitle;
static Long afterCount;

    @BeforeAll
    static void beforeAll() throws IOException {
        productService = RetrofitUtils.getRetrofit().create(ProductService.class);
        SqlSession session = null;

        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sqlSessionFactory = new
                    SqlSessionFactoryBuilder().build(inputStream);
            session = sqlSessionFactory.openSession();
            db.dao.ProductsMapper productsMapper = session.getMapper(db.dao.ProductsMapper.class);

            db.model.ProductsExample example = new db.model.ProductsExample();
            //example.createCriteria().andIdEqualTo(2L);
            List<db.model.Products> list = productsMapper.selectByExample(example);

            afterTitle= list.get(0).getTitle();
            afterCount=productsMapper.countByExample(example);
            session.commit();
        }
        finally {
            session.close();
        }
    }

    @SneakyThrows
    @Test
    void getCategoryByIdPositiveTest() {
        Response<ResponseBody> response = productService.getProducts().execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().string(), containsString(afterTitle));
    }
}
