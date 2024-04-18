package com.eshopapi.eshopapi.controller.product;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eshopapi.eshopapi.EshopApiApplication;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = EshopApiApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class POSTProductControllerIT {
  private final String inital_db_products =
      "[{\"productId\":1,\"name\":\"Fancy IPA Beer\",\"price\":5.99,\"addedAt\":\"2022/09/01\",\"labels\":[\"drink\",\"limited\"]},{\"productId\":2,\"name\":\"Artisanal Sourdough Bread\",\"price\":3.5,\"addedAt\":\"2022/09/02\",\"labels\":[\"food\"]},{\"productId\":3,\"name\":\"Organic Blueberry Jam\",\"price\":4.25,\"addedAt\":\"2022/08/25\",\"labels\":[\"food\"]},{\"productId\":4,\"name\":\"Handcrafted Wool Sweater\",\"price\":45.0,\"addedAt\":\"2022/10/10\",\"labels\":[\"clothes\"]},{\"productId\":5,\"name\":\"Limited Edition Coffee Mug\",\"price\":12.0,\"addedAt\":\"2022/11/01\",\"labels\":[\"drink\",\"limited\"]},{\"productId\":6,\"name\":\"Vintage Vinyl Record\",\"price\":17.99,\"addedAt\":\"2022/07/15\",\"labels\":[\"limited\"]}]";
  @Autowired private MockMvc mockMvc;

  /*Test that calls POST "products"
   * then calls GET "products" by ID to verify the product was added*/
  @Test
  public void testAddProduct() throws Exception {
    final String request =
        """
              {
                  "name": "Cheap and cheerful lager beer",
                  "price": 3.99,
                  "labels": [
                      "drink"
                  ]
              }""";
    final String expectedResponse =
        String.format(
            "{\"productId\":7,\"name\":\"Cheap and cheerful lager beer\",\"price\":3.99,\"addedAt\":\"%s\",\"labels\":[\"drink\"]}",
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isCreated())
        .andExpect(content().string(expectedResponse));
    // now retrieve the product to verify it was added to the db
    mockMvc
        .perform(MockMvcRequestBuilders.get("/products/7"))
        .andExpect(status().isOk())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls POST "products" but the name already exists*/
  @Test
  public void testAddProductNameNotUnique() throws Exception {
    final String request =
        """
              {
                  "name": "Cheap and cheerful lager beer",
                  "price": 3.99,
                  "labels": [
                      "drink"
                  ]
              }""";
    final String expectedResponse =
        String.format(
            "{\"productId\":7,\"name\":\"Cheap and cheerful lager beer\",\"price\":3.99,\"addedAt\":\"%s\",\"labels\":[\"drink\"]}",
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isCreated())
        .andExpect(content().string(expectedResponse));
    //  Now try and add the same product again
    final String expectedErrorResponse =
        "{\"status\":400,\"error\":\"Product name already exists\",\"message\":\"A product with the same name already exists\",\"path\":\"uri=/products\"}";
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(expectedErrorResponse));
  }

  /*Test that calls POST "products" but the name is empty*/
  @Test
  public void testAddProductNameEmpty() throws Exception {
    final String request =
        """
              {
                  "name": "",
                  "price": 3.99,
                  "labels": [
                      "drink"
                  ]
              }""";
    final String expectedResponse =
        "{\"status\":400,\"error\":\"Validation Error\",\"message\":\"name: must not be empty\",\"path\":\"uri=/products\"}";
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls POST "products" but the name is null*/
  @Test
  public void testAddProductNameNull() throws Exception {
    final String request =
        """
              {
                  "price": 3.99,
                  "labels": [
                      "drink"
                  ]
              }""";
    final String expectedResponse =
        "{\"status\":400,\"error\":\"Validation Error\",\"message\":\"name: must not be empty\",\"path\":\"uri=/products\"}";
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls POST "products" but the name exceeds 200 characters*/
  @Test
  public void testAddProductNameInvalid() throws Exception {
    final String request =
        String.format(
            """
              {
                  "name": "%s",
                  "price": 3.99,
                  "labels": [
                      "drink"
                  ]
              }""",
            "Cheap and cheerful lager beer)".repeat(100));
    final String expectedResponse =
        "{\"status\":400,\"error\":\"Validation Error\",\"message\":\"name: Product name must not exceed 200 characters\",\"path\":\"uri=/products\"}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls POST "products" but some of the labels are invalid */
  @Test
  public void testAddProductLabelsInvalid() throws Exception {
    final String request =
        String.format(
            """
              {
                  "name": "Cheap and cheerful lager beer",
                  "price": 3.99,
                  "labels": [
                      "%1$s",
                      "%2$s"
                  ]
              }""",
            "food", "invalid");
    final String expectedResponse =
        "{\"status\":400,\"error\":\"Label not valid\",\"message\":\"Label is not valid\",\"path\":\"uri=/products\"}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls POST "products" but all the labels are invalid */
  @Test
  public void testAddProductLabelsAllInvalid() throws Exception {
    final String request =
        String.format(
            """
              {
                  "name": "Cheap and cheerful lager beer",
                  "price": 3.99,
                  "labels": [
                      "%1$s",
                      "%2$s"
                  ]
              }""",
            "test", "invalid");
    final String expectedResponse =
        "{\"status\":400,\"error\":\"Label not valid\",\"message\":\"Label is not valid\",\"path\":\"uri=/products\"}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls POST "products" but the price is not numerical*/
  @Test
  public void testAddProductPriceInvalid() throws Exception {
    final String request =
        """
              {
                  "name": "Cheap and cheerful lager beer",
                  "price": "abc",
                  "labels": [
                      "drink"
                  ]
              }""";
    final String expectedResponse =
        "{\"status\":400,\"error\":\"Parse Error\",\"message\":\"JSON parse error: Cannot deserialize value of type `java.math.BigDecimal` from String \\\"abc\\\": not a valid representation\",\"path\":\"uri=/products\"}";
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls POST "products" but the price is empty*/
  @Test
  public void testAddProductPriceEmpty() throws Exception {
    final String request =
        """
              {
                  "name": "Cheap and cheerful lager beer",
                  "price": "",
                  "labels": [
                      "drink"
                  ]
              }""";
    final String expectedResponse =
        "{\"status\":400,\"error\":\"Validation Error\",\"message\":\"price: must not be null\",\"path\":\"uri=/products\"}";
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls POST "products" but the price is null*/
  @Test
  public void testAddProductPriceNull() throws Exception {
    final String request =
        """
              {
                  "name": "Cheap and cheerful lager beer",
                  "labels": [
                      "drink"
                  ]
              }""";
    final String expectedResponse =
        "{\"status\":400,\"error\":\"Validation Error\",\"message\":\"price: must not be null\",\"path\":\"uri=/products\"}";
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls POST "products" but the price is too precise ( > 2dp )*/
  @Test
  public void testAddProductPriceTooPrecise() throws Exception {
    final String request =
        """
              {
                  "name": "Cheap and cheerful lager beer",
                  "price": 420.725,
                  "labels": [
                      "drink"
                  ]
              }""";
    final String expectedResponse =
        "{\"status\":400,\"error\":\"Validation Error\",\"message\":\"price: numeric value out of bounds (<10 digits>.<2 digits> expected)\",\"path\":\"uri=/products\"}";
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls POST "products" but the price is -ve*/
  @Test
  public void testAddProductPriceNegative() throws Exception {
    final String request =
        """
                  {
                      "name": "Cheap and cheerful lager beer",
                      "price": -420.7,
                      "labels": [
                          "drink"
                      ]
                  }""";
    final String expectedResponse =
        "{\"status\":400,\"error\":\"Validation Error\",\"message\":\"price: must be greater than 0\",\"path\":\"uri=/products\"}";
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(expectedResponse));
  }
}
