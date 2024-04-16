package com.eshopapi.eshopapi.controller;

import com.eshopapi.eshopapi.EshopApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = EshopApiApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DELETEProductControllerIT {
  private final String inital_db_products =
      "[{\"productId\":1,\"name\":\"Fancy IPA Beer\",\"price\":5.99,\"addedAt\":\"2022/09/01\",\"labels\":[\"drink\",\"limited\"]},{\"productId\":2,\"name\":\"Artisanal Sourdough Bread\",\"price\":3.5,\"addedAt\":\"2022/09/02\",\"labels\":[\"food\"]},{\"productId\":3,\"name\":\"Organic Blueberry Jam\",\"price\":4.25,\"addedAt\":\"2022/08/25\",\"labels\":[\"food\"]},{\"productId\":4,\"name\":\"Handcrafted Wool Sweater\",\"price\":45.0,\"addedAt\":\"2022/10/10\",\"labels\":[\"clothes\"]},{\"productId\":5,\"name\":\"Limited Edition Coffee Mug\",\"price\":12.0,\"addedAt\":\"2022/11/01\",\"labels\":[\"drink\",\"limited\"]},{\"productId\":6,\"name\":\"Vintage Vinyl Record\",\"price\":17.99,\"addedAt\":\"2022/07/15\",\"labels\":[\"limited\"]}]";
  @Autowired private MockMvc mockMvc;

  /*Test that calls DELETE "products" */
  @Test
  public void testDeleteProduct() throws Exception {
    final String productID = "1";
    final String expectedResponse =
            "Product 1 successfully deleted";
    mockMvc
            .perform(MockMvcRequestBuilders.delete("/products/" + productID))
            .andExpect(status().isOk())
            .andExpect(content().string(expectedResponse));

    // now try to get the product we just deleted
    final String expectedErrorResponse = "{\"status\":404,\"error\":\"Product could not be found\",\"message\":\"Product with ID 1 not found.\",\"path\":\"uri=/products/1\"}";
    mockMvc
            .perform(MockMvcRequestBuilders.get("/products/" + productID))
            .andExpect(status().isNotFound())
            .andExpect(content().string(expectedErrorResponse));
  }

  /*Test that calls DELETE "products" but the id doesn't exist */
  @Test
  public void testDeleteProductInvalid() throws Exception {
    final String productID = "100";
    final String expectedResponse = "{\"status\":404,\"error\":\"Product could not be found\",\"message\":\"Product with ID 100 not found.\",\"path\":\"uri=/products/100\"}";
    mockMvc
            .perform(MockMvcRequestBuilders.delete("/products/" + productID))
            .andExpect(status().isNotFound())
            .andExpect(content().string(expectedResponse));
  }
}
