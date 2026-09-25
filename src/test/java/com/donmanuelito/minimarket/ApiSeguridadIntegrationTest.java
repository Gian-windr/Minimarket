package com.donmanuelito.minimarket;

import com.donmanuelito.minimarket.dto.LoginRequest;
import com.donmanuelito.minimarket.dto.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Levanta Tomcat de verdad y comprueba el circuito HTTP completo:
 * proteccion de endpoints, login con JWT y acceso autenticado.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiSeguridadIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    @DisplayName("Sin token los endpoints responden 401 con JSON")
    void sinTokenDevuelve401() {
        ResponseEntity<String> respuesta = rest.getForEntity("/api/productos", String.class);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(respuesta.getBody()).contains("No autenticado");
    }

    @Test
    @DisplayName("Login con credenciales validas devuelve un token utilizable")
    void loginDevuelveTokenYPermiteConsultar() {
        ResponseEntity<LoginResponse> login = rest.postForEntity(
                "/api/auth/login", new LoginRequest("admin", "admin123"), LoginResponse.class);

        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(login.getBody()).isNotNull();
        assertThat(login.getBody().token()).isNotBlank();
        assertThat(login.getBody().rol()).isEqualTo("ADMINISTRADOR");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login.getBody().token());
        ResponseEntity<String> productos = rest.exchange(
                "/api/productos", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertThat(productos.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Credenciales incorrectas devuelven 401")
    void loginConPasswordIncorrectaFalla() {
        ResponseEntity<String> respuesta = rest.postForEntity(
                "/api/auth/login", new LoginRequest("admin", "password-incorrecta"), String.class);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("La documentacion Swagger es publica")
    void swaggerEsAccesibleSinToken() {
        ResponseEntity<String> respuesta = rest.getForEntity("/v3/api-docs", String.class);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).contains("MiniMarket");
    }
}
