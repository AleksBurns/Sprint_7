import static org.hamcrest.Matchers.*;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;

/**
 * Тест:    Логин курьера
 * Проверь:
 * курьер может авторизоваться;
 * для авторизации нужно передать все обязательные поля;
 * система вернёт ошибку, если неправильно указать логин или пароль;
 * если какого-то поля нет, запрос возвращает ошибку;
 * если авторизоваться под несуществующим пользователем, запрос возвращает ошибку;
 * успешный запрос возвращает id.
 */

public class CourierLoginTests {
    Courier courier = new Courier(
            "AleksBurns",
            "1111"
    );
    Courier courierWithoutPassword = new Courier("AleksBurns", "");
    Courier courierWithoutLogin = new Courier("", "1111");

    @Before
    public void setUp() {
        RestAssured.baseURI = URI.BASE_URI;
    }

    @Step("Отправка запроса для создания курьера")
    public void sendRequestToCreateCourier(Courier courier) {
        given().header("Content-type", "application/json").body(courier).when().post("/api/v1/courier");
    }
    @Step("Отправка запроса для логина курьера")
    public Response loginCourier(Courier courier){
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(courier)
                        .post(URI.COURIER_LOGIN);
        return response;
    }
    @Step("Проверка: успешный запрос возвращает id")
    public void checkIdInResponse (Response response){
        response.then().assertThat().body("id", notNullValue());
    }
    @Step("Проверка ошибки при логине без обязательного поля")
    public void checkErrorWithEmptyField(Response response){
        response.then().assertThat().body("message", startsWith("Недостаточно данных для входа"));
    }
    @Step("Проверка ошибки при логине с неверными данными")
    public void checkErrorWithInvalidValue(Response response){
        response.then().assertThat().body("message", startsWith("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Проверка возможности авторизоваться и наличия id в ответе")
    public void loginCourier() {
        sendRequestToCreateCourier(courier);
        Response response = loginCourier(courier);
        checkIdInResponse(response);
    }

    @Test
    @DisplayName("Проверка: для авторизации нужно передать все обязательные поля;  если какого-то поля нет, запрос возвращает ошибку;")
    public void loginWithEmptyField() {
        Response requestWithoutLogin = loginCourier(courierWithoutLogin);
        checkErrorWithEmptyField(requestWithoutLogin);
        Response requestWithoutPassword = loginCourier(courierWithoutPassword);
        checkErrorWithEmptyField(requestWithoutPassword);
    }

    @Test
    @DisplayName("Проверка: При попытке авторизоваться под несуществующим пользователем, запрос возвращает ошибку")
    public void loginWithInvalidValue() {
        Response response = loginCourier(courier);
        checkErrorWithInvalidValue(response);
    }

    @After
    public void deleteCourier() {
        Courier id = given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier/login")
                .as(Courier.class);
        given().delete(URI.DELETE_COURIER + id.getId());
    }
}