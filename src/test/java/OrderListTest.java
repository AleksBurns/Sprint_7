import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;


/**
 * Тест:    Список заказов
 * Проверь, что в тело ответа возвращается список заказов.
 */

public class OrderListTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = URI.BASE_URI;
    }
    @Step("Отправка запроса на получение списка заказов.")
    public Response orderListRequest() {
        Response response =
                given()
                        .get(URI.GET_ORDER_LIST);
        return response;
    }
    @Step("Проверка наличия списка заказов в ответе")
    public void checkOrderListInResponse(Response response){
        response
                .then()
                .assertThat()
                .body("orders", notNullValue());
    }

    @Test
    @DisplayName("Тест: В теле ответа отображается список заказов.")
    public void checkOrderList(){
        Response response = orderListRequest();
        checkOrderListInResponse(response);
    }
}