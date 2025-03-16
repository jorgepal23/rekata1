package test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.Utils;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.equalTo;

public class PutProduct {
    @Test
    public void testPutProductSuccess() throws Exception {
        String body = new String(Files.readAllBytes(Paths.get("src/test/resources/body.json")));

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put("/1") // Modificar producto con ID 1
                .then()
                .statusCode(200)
                .body("title", equalTo("Nuevo Producto"))
                .extract().response();

        Utils.printJsonResponse("PUT Response:", response); // 📌 Imprimir con JSON formateado

        // Asserts adicionales
        Assert.assertEquals(response.getStatusCode(), 200, "Código de respuesta incorrecto");
        Assert.assertNotNull(response.jsonPath().get("id"), "El ID del producto actualizado es nulo");
        Assert.assertEquals(response.jsonPath().get("title"), "Nuevo Producto", "El título del producto no fue actualizado correctamente");
    }

    @Test(priority = 5)
    public void testPutProductWrong() throws Exception {
        // Leer el JSON del archivo
        String body = new String(Files.readAllBytes(Paths.get("src/test/resources/body.json")));

        // Enviar la solicitud con un endpoint inválido
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put("/hola") // Endpoint incorrecto
                .then()
                .extract().response();

        // Imprimir la respuesta formateada
        Utils.printJsonResponse("PUT Invalid Response:", response);

        // Obtener el código de respuesta
        int statusCode = response.getStatusCode();
        System.out.println("Código de respuesta: " + statusCode);

        // Validar que el código de respuesta sea 400 o 404, dependiendo de la API
        Assert.assertTrue(statusCode == 400 || statusCode == 404,
                "❌ Código de respuesta inesperado: " + statusCode);

        // Verificar que la respuesta no contiene datos de producto
        Assert.assertNull(response.jsonPath().get("id"), "El ID no debería existir en la respuesta");
        Assert.assertNull(response.jsonPath().get("title"), "El título no debería existir en la respuesta");

        // Si la API devuelve un mensaje de error, verificarlo
        if (response.jsonPath().get("message") != null) {
            System.out.println("Mensaje de error recibido: " + response.jsonPath().get("message"));
            Assert.assertFalse(response.jsonPath().get("message").toString().isEmpty(),
                    "El mensaje de error no debería estar vacío");
        }
    }

}
