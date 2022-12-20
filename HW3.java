package HWJB3;

import io.qameta.allure.*;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

public class HW3 {

    //static Map<String, String> headers = new HashMap<>();   Для Авторизации
    static Properties properties = new Properties();

    @BeforeAll
    static void setUp() throws IOException {
        RestAssured.filters(new AllureRestAssured());
        //headers.put("Autorization", "Bearer 993rfdscvo439"); //для Авторизации
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/my.properties");
        properties.load(fileInputStream);
    }

   /* @AfterEach
    void tearDown() {
        headers.clear();
    }
    */

    //Перечень пользователей
    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Перечень пользователей: Все пользователи")
    @Feature(value = "Перечень пользователей")
    void getAllUsers() {
        given()
                .when()
                .get((String) properties.get("URL"))
                .then()
                .statusCode(200).and().body("data", notNullValue());
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Перечень пользователей: Один пользователь")
    @Feature(value = "Перечень пользователей")
    void getSingleUser() {

        given()
                .when()
                .get((String) properties.get("URL") + "/api/users/" + (String) properties.get("userid"))
                .then()
                .statusCode(200)
                .and()
                .extract()
                .response()
                .jsonPath()
                .getString("data")
                .compareTo((String) properties.get("JanetExpectedResponse"));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Перечень пользователей: Не найден пользователь")
    @Feature(value = "Перечень пользователей")
    void getUserNotFound() {
        given()
                .when()
                .get((String) properties.get("URL") + "/api/users/" + (String) properties.get("useridNOTfound"))
                .then()
                .statusCode(404);
    }

//Регистрация нового пользователя

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Создание нового пользователя")
    @Story(value = "Регистрация")
    void createNewUser() {
        given()
                .contentType("application/json")
                .body(properties.get("createEvgen"))
                .when()
                .post((String) properties.get("URL") + "/api/users/")
                .then()
                .statusCode(201)
                //Вариант 1 через body
                .and()
                .body("name", equalTo("Evgen"))
                .and()
                .body("job", equalTo("GB_Student"))
                //Вариант 2 через extract
                .and()
                .extract()
                .response()
                .jsonPath()
                .getString("")
                .compareTo((String) properties.get("createEvgen"));
    }

//Войти

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Войти: Регистрация (валидная)")
    @Story(value = "Вход")
    void registrationValid() {
        given()
                .contentType("application/json")
                .body(properties.get("registrationValid"))
                .when()
                .post((String) properties.get("URL") + "/api/register")
                .then()
                .statusCode(200)
                //Вариант 1 через body
                .and()
                .body("id", equalTo(4))
                .and()
                .body("token", notNullValue())
                //Вариант 2 через extract
                .and()
                .extract()
                .response()
                .jsonPath()
                .getString("")
                .compareTo((String) properties.get("registrationValid"));
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Войти: Регистрация (не валидная)")
    @Story(value = "Вход")
    void registrationNotValid() {
        given()
                .contentType("application/json")
                .body(properties.get("registrationNotValid"))
                .when()
                .post((String) properties.get("URL") + "/api/register")
                .then()
                .statusCode(400)
                .and()
                .body("error", equalTo("Missing password"));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Войти: Вход (валидный)")
    @Story(value = "Вход")
    void LoginValid() {
        given()
                .contentType("application/json")
                .body(properties.get("LoginValid"))
                .when()
                .post((String) properties.get("URL") + "/api/login")
                .then()
                .statusCode(200)
                .and()
                .body("token", notNullValue());
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Войти: Вход (не валидный)")
    @Story(value = "Вход")
    void LoginNotValid() {
        given()
                .contentType("application/json")
                .body(properties.get("LoginNotValid"))
                .when()
                .post((String) properties.get("URL") + "/api/login")
                .then()
                .statusCode(400)
                .and()
                .body("error", equalTo("Missing password"));
    }

//Изменить данные пользователя

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Удаление пользователя")
    @Epic(value = "Изменить данные")
    void changeUserDataPUT() {
        given()
                .contentType("application/json")
                .body(properties.get("changeUserDataPUT"))
                .when()
                .put((String) properties.get("URL") + "/api/users/" + (String) properties.get("userid"))
                .then()
                .statusCode(200)
                .and()
                .body("job", equalTo("JUST_Student"));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Epic(value = "Изменить данные")
    void changeUserDataPATCH() {
        given()
                .contentType("application/json")
                .body(properties.get("changeUserDataPATCH"))
                .when()
                .put((String) properties.get("URL") + "/api/users/" + (String) properties.get("userid"))
                .then()
                .statusCode(200)
                .and()
                .body("job", equalTo("VIP_Student"));
    }

//Удаление пользователя

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story(value = "Удаление")
    void deleteUser() {
        given()
                .when()
                .delete((String) properties.get("URL") + "/api/users/" + (String) properties.get("userid"))
                .then()
                .statusCode(204);
    }
}
