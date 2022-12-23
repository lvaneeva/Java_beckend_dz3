package lesson6;

import Lesson5.api.CategoryService;
import Lesson5.dto.GetCategoryResponse;
import Lesson5.utils.RetrofitUtils;
import io.restassured.internal.common.assertion.Assertion;
import lombok.SneakyThrows;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GetCategoryTest {

    static CategoryService categoryService;
    static int expectedData;
    @BeforeAll
    static void beforeAll() throws IOException {
        categoryService = RetrofitUtils.getRetrofit().create(CategoryService.class);
        SqlSession session = null;
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sqlSessionFactory = new
                    SqlSessionFactoryBuilder().build(inputStream);
            session = sqlSessionFactory.openSession();
            db.dao.CategoriesMapper categoriesMapper = session.getMapper(db.dao.CategoriesMapper.class);
            db.model.CategoriesExample example = new db.model.CategoriesExample();

            example.createCriteria().andIdEqualTo(1L);
            List<db.model.Categories> list = categoriesMapper.selectByExample(example);
            expectedData= (int) categoriesMapper.countByExample(example);
          //  System.out.println(categoriesMapper.countByExample(example));
        } finally {
            session.close();
        }

    }

    @SneakyThrows
    @Test
    void getCategoryByIdPositiveTest() {
        Response<GetCategoryResponse> response = categoryService.getCategory(1).execute();

        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getId(), equalTo(1));
        assertThat(response.body().getTitle(), equalTo("Food"));
        response.body().getProducts().forEach(product ->
                assertThat(product.getCategoryTitle(), equalTo("Food")));

        assertEquals(expectedData, response.body().getId());
        //assertNotNull(expectedData);
        System.out.println(expectedData);
    }

}
