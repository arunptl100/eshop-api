package com.eshopapi.eshopapi.controller.cart;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eshopapi.eshopapi.EshopApiApplication;
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
class PUTCartControllerIT {
  private final String initial_db_carts =
      "[{\"cartId\":1,\"products\":[{\"productId\":1,\"quantity\":2},{\"productId\":3,\"quantity\":1}],\"checkedOut\":false},{\"cartId\":2,\"products\":[{\"productId\":2,\"quantity\":3},{\"productId\":4,\"quantity\":1}],\"checkedOut\":true},{\"cartId\":3,\"products\":[{\"productId\":5,\"quantity\":1},{\"productId\":6,\"quantity\":2}],\"checkedOut\":false}]";
  @Autowired private MockMvc mockMvc;

  /*Test that calls PUT "/carts/:id"*/
  @Test
  public void testPutCart() throws Exception {
    //  cart 1 initially does not have product 2 in the cart
    //  After updating we expect the cart to have product 2 with quantity 3, along with its other
    // existing items
    final String request =
        """
            [
                {
                    "productId": 2,
                    "quantity": 3
                }
            ]""";

    final String expectedResponse =
        "{\"cartId\":1,\"products\":[{\"productId\":1,\"quantity\":2},{\"productId\":3,\"quantity\":1},{\"productId\":2,\"quantity\":3}],\"checkedOut\":false}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/carts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isOk())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls PUT "/carts/:id" with a product already in the cart*/
  @Test
  public void testPutCartExistingProduct() throws Exception {
    //  cart 1 initially has product 1 in the cart with a quantity of 2
    //  After updating (adding 3), we expect the cart to have product 1 with a quantity of 5
    final String request =
        """
            [
                {
                    "productId": 1,
                    "quantity": 3
                }
            ]""";

    final String expectedResponse =
        "{\"cartId\":1,\"products\":[{\"productId\":1,\"quantity\":5},{\"productId\":3,\"quantity\":1}],\"checkedOut\":false}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/carts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isOk())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls PUT "/carts/:id" with a negative quantity*/
  @Test
  public void testPutCartNegativeQuantity() throws Exception {
    final String request =
        """
            [
                {
                    "productId": 2,
                    "quantity": -3
                }
            ]""";

    final String expectedResponse =
        "{\"status\":400,\"error\":\"Validation Error\",\"message\":\"quantity must be greater than 0\",\"path\":\"uri=/carts/1\"}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/carts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls PUT "/carts/:id" on a cart that doesnt exist*/
  @Test
  public void testPutCartInvalidCart() throws Exception {
    final String request =
        """
            [
                {
                    "productId": 2,
                    "quantity": 3
                }
            ]""";

    final String expectedResponse =
        "{\"status\":404,\"error\":\"Cart could not be found\",\"message\":\"Cart not found\",\"path\":\"uri=/carts/100\"}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/carts/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isNotFound())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls PUT "/carts/:id" on a cart thats already checked out*/
  @Test
  public void testPutCartAlreadyCheckedOut() throws Exception {
    //  cart 2 is already checked out, so we expect an error response
    final String request =
        """
            [
                {
                    "productId": 2,
                    "quantity": 3
                }
            ]""";

    final String expectedResponse =
        "{\"status\":400,\"error\":\"Cart already checked out\",\"message\":\"Cannot modify a checked out cart\",\"path\":\"uri=/carts/2\"}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/carts/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls PUT "/carts/:id" with a product that doesnt exist*/
  @Test
  public void testPutCartInvalidProduct() throws Exception {
    //  cart 2 is already checked out, so we expect an error response
    final String request =
        """
            [
                {
                    "productId": 420,
                    "quantity": 3
                }
            ]""";

    final String expectedResponse =
        "{\"status\":404,\"error\":\"Product could not be found\",\"message\":\"Product not found with ID: 420\",\"path\":\"uri=/carts/1\"}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/carts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isNotFound())
        .andExpect(content().string(expectedResponse));
  }
}
