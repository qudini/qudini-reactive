package com.qudini.reactive.graphql;

import com.qudini.reactive.graphql.app.TestApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes = {TestApplication.class, ReactiveGraphQLAutoConfiguration.class})
@AutoConfigureWebTestClient
@DisplayName("qudini-reactive-graphql")
class ReactiveGraphQLTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("should allow resolving unions/interfaces")
    void resolveInterface() {
        webTestClient
                .post()
                .uri("/graphql")
                .contentType(APPLICATION_JSON)
                .bodyValue(Map.of("query", "query { myFooBars { name } }"))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.myFooBars.length()").isEqualTo(2)
                .jsonPath("$.data.myFooBars[0].name").isEqualTo("MyFoo")
                .jsonPath("$.data.myFooBars[1].name").isEqualTo("MyBar");
    }

    @Test
    @DisplayName("should allow boolean-based custom scalars")
    void booleanScalar() {
        webTestClient
                .post()
                .uri("/graphql")
                .contentType(APPLICATION_JSON)
                .bodyValue(Map.of(
                        "query", "query($foo: MyBoolean!, $bar: MyBoolean!) { foo: myBoolean(in: $foo) bar: myBoolean(in: $bar) }",
                        "variables", Map.of(
                                "foo", false,
                                "bar", true
                        )
                ))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.foo").isEqualTo("FOO")
                .jsonPath("$.data.bar").isEqualTo("BAR");
    }

    @Test
    @DisplayName("should allow string-based custom scalars")
    void stringScalar() {
        webTestClient
                .post()
                .uri("/graphql")
                .contentType(APPLICATION_JSON)
                .bodyValue(Map.of(
                        "query", "query($foo: MyString!, $bar: MyString!) { foo: myString(in: $foo) bar: myString(in: $bar) }",
                        "variables", Map.of(
                                "foo", "foo",
                                "bar", "bar"
                        )
                ))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.foo").isEqualTo("FOO")
                .jsonPath("$.data.bar").isEqualTo("BAR");
    }

    @Test
    @DisplayName("should allow int-based custom scalars")
    void intScalar() {
        webTestClient
                .post()
                .uri("/graphql")
                .contentType(APPLICATION_JSON)
                .bodyValue(Map.of(
                        "query", "query($foo: MyInt!, $bar: MyInt!) { foo: myInt(in: $foo) bar: myInt(in: $bar) }",
                        "variables", Map.of(
                                "foo", -1,
                                "bar", 1
                        )
                ))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.foo").isEqualTo("FOO")
                .jsonPath("$.data.bar").isEqualTo("BAR");
    }

    @Test
    @DisplayName("should allow float-based custom scalars")
    void floatScalar() {
        webTestClient
                .post()
                .uri("/graphql")
                .contentType(APPLICATION_JSON)
                .bodyValue(Map.of(
                        "query", "query($foo: MyFloat!, $bar: MyFloat!) { foo: myFloat(in: $foo) bar: myFloat(in: $bar) }",
                        "variables", Map.of(
                                "foo", -1.1F,
                                "bar", 1.1F
                        )
                ))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.foo").isEqualTo("FOO")
                .jsonPath("$.data.bar").isEqualTo("BAR");
    }

    @Test
    @DisplayName("should make the reactive context available to the resolvers")
    void reactiveContext() {
        webTestClient
                .post()
                .uri("/graphql")
                .contentType(APPLICATION_JSON)
                .bodyValue(Map.of("query", "query { context }"))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.context").isEqualTo("helloworld");
    }

}
