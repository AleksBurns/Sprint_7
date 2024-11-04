import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import java.util.List;

/**
 * Тест:    Создание заказа
 * Проверь, что когда создаёшь заказ:
 * можно указать один из цветов — BLACK или GREY;
 * можно указать оба цвета;
 * можно совсем не указывать цвет;
 * тело ответа содержит track.
 * Чтобы протестировать создание заказа, нужно использовать параметризацию.
 */

@RunWith(Parameterized.class)

public class OrderCreateTests {
    private final List<String> colorParam;

    public OrderCreateTests(List<String> colorParam) {
        this.colorParam = colorParam;
    }

    @Parameterized.Parameters(name = "Цвет самоката: {0}")
    public static Object[][] getColorParam() {
        return new Object[][]{
                {List.of("BLACK","GREY")},
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of()}
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = URI.BASE_URI;
    }

    @Step("Создание заказа")
    public Response createOrderRequest(Order order) {
        Response response =
                given()
                        .header("Content-type","application/json")
                        .body(order)
                        .when()
                        .post(URI.CREATE_ORDER);
        return response;
    }
    @Step("Проверка что тело ответа содержит track")
    public void checkTrackInResponse(Response response){
        response.then().assertThat().body("track", notNullValue());
    }
    @Step("Получение номера трека заказа")
    public void getOrderTrack(Order order){
        Order track = given()
                .header("Content-type", "application/json")
                .body(order)
                .post(URI.CREATE_ORDER)
                .as(Order.class);
        System.out.println(track.getTrack());
    }

    @Test
    @DisplayName("Тест: Заказ можно создать с доступными цветами самоката и без них, при заказе отображается трек-номер заказа")
    public void createOrder(){
        Order order = new Order(
                "Aleks",
                "Burns",
                "Testing st.11/11",
                "42",
                "+74815162342",
                4,
                "10.11.2024",
                "for gift",
                colorParam
        );
        Response response = createOrderRequest(order);
        checkTrackInResponse(response);
        getOrderTrack(order);
    }
}