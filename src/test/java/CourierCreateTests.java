import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Тест:    Создание курьера
 * Проверь:
 * курьера можно создать;
 * нельзя создать двух одинаковых курьеров;
 * чтобы создать курьера, нужно передать в ручку все обязательные поля;
 * запрос возвращает правильный код ответа;
 * успешный запрос возвращает ok: true;
 * если одного из полей нет, запрос возвращает ошибку;
 * если создать пользователя с логином, который уже есть, возвращается ошибка.
 */
public class CourierCreateTests {
    Courier courier = new Courier(
            "AleksBurns",
            "1111",
            "Aleks"
    );
    Courier courierWithoutLogin = new Courier(null,"1111");
    Courier courierWithoutPassword = new Courier("Aleks", null);

    @Before
    public void setUp() {
        RestAssured.baseURI = URI.BASE_URI;
    }

    @Step("Отправка запроса для создания курьера")
    public Response sendRequestToCreateCourier(Courier courier) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(courier)
                        .when()
                        .post(URI.CREATE_COURIER);
        return response;
    }
    @Step("Проверка кода ответа 201")
    public void checkResponseCode201(Response response){
        response.then().assertThat().statusCode(201);
    }
    @Step("Проверка, что тело ответа ok: true")
    public void checkResponseBodyOkTrue(Response response){
        response.then().assertThat().body("ok", equalTo(true));
    }
    @Step("Проверка кода ответа 409")
    public void checkResponseCode409(Response response) {
        response.then().assertThat().statusCode(409);
    }
    @Step("Проверка сообщения об ошибке при дублировании курьера")
    public void checkDuplicateMessage(Response response) {
        response.then().assertThat().body("message", startsWith("Этот логин уже используется"));
    }
    @Step("Проверка сообщения об ошибке при незаполненном поле")
    public void checkEmptyFieldMessage(Response response) {
        response.then().assertThat().body("message", startsWith("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Тест: Курьера можно создать")
    @Description("Курьера можно создать; Чтобы создать курьера, нужно передать в ручку все обязательные поля; Запрос возвращает правильный код ответа; Успешный запрос возвращает ok: true;")
    public void createCourier(){
        Response response = sendRequestToCreateCourier(courier);
        checkResponseCode201(response);
        checkResponseBodyOkTrue(response);
    }
    @Test
    @DisplayName("Тест: Нельзя создать двух одинаковых курьеров")
    @Description("Нельзя создать двух одинаковых курьеров; Запрос возвращает правильный код ответа; Если создать пользователя с логином, который уже есть, возвращается ошибка.")
    public void duplicateCourier(){
        Response response = sendRequestToCreateCourier(courier);
        Response response2 = sendRequestToCreateCourier(courier);
        checkResponseCode409(response2);
        checkDuplicateMessage(response2);
    }
    @Test
    @DisplayName("Тест: Если одного из полей при создании нет, запрос возвращает ошибку")
    @Description("Если одного из полей нет, запрос возвращает ошибку;")
    public void emptyFieldError(){
        Response requestWithoutLogin = sendRequestToCreateCourier(courierWithoutLogin);
        checkEmptyFieldMessage(requestWithoutLogin);
        Response requestWithoutPassword = sendRequestToCreateCourier(courierWithoutPassword);
        checkEmptyFieldMessage(requestWithoutPassword);
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