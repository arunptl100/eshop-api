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
class POSTCartControllerIT {
  private final String initial_db_carts =
      "[{\"cartId\":1,\"products\":[{\"productId\":1,\"quantity\":2},{\"productId\":3,\"quantity\":1}],\"checkedOut\":false},{\"cartId\":2,\"products\":[{\"productId\":2,\"quantity\":3},{\"productId\":4,\"quantity\":1}],\"checkedOut\":true},{\"cartId\":3,\"products\":[{\"productId\":5,\"quantity\":1},{\"productId\":6,\"quantity\":2}],\"checkedOut\":false}]";
  @Autowired private MockMvc mockMvc;

  /*Test that calls POST "carts" */
  @Test
  public void testAddCart() throws Exception {
    final String request =
        """
            [
                {
                    "productId": 1,
                    "quantity": 3
                },
                {
                    "productId": 2,
                    "quantity": 1
                },
                {
                    "productId": 4,
                    "quantity": 5
                }
            ]""";
    final String expectedResponse =
        "{\"cartId\":4,\"products\":[{\"productId\":1,\"quantity\":3},{\"productId\":2,\"quantity\":1},{\"productId\":4,\"quantity\":5}],\"checkedOut\":false}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isCreated())
        .andExpect(content().string(expectedResponse));

    //  get carts and verify the new cart was created and stored
    final String expectedGETResponse =
        "[{\"cartId\":1,\"products\":[{\"productId\":1,\"quantity\":2},{\"productId\":3,\"quantity\":1}],\"checkedOut\":false},{\"cartId\":2,\"products\":[{\"productId\":2,\"quantity\":3},{\"productId\":4,\"quantity\":1}],\"checkedOut\":true},{\"cartId\":3,\"products\":[{\"productId\":5,\"quantity\":1},{\"productId\":6,\"quantity\":2}],\"checkedOut\":false},{\"cartId\":4,\"products\":[{\"productId\":1,\"quantity\":3},{\"productId\":2,\"quantity\":1},{\"productId\":4,\"quantity\":5}],\"checkedOut\":false}]";
    mockMvc
        .perform(MockMvcRequestBuilders.get("/carts"))
        .andExpect(status().isOk())
        .andExpect(content().string(expectedGETResponse));
  }

  /*Test that calls POST "carts" but with a product that doesnt exist */
  @Test
  public void testAddCartInvalidProduct() throws Exception {
    final String request =
        """
            [
                {
                    "productId": 1,
                    "quantity": 3
                },
                {
                    "productId": 2,
                    "quantity": 1
                },
                {
                    "productId": 420,
                    "quantity": 5
                }
            ]""";
    final String expectedResponse =
        "{\"status\":404,\"error\":\"Product could not be found\",\"message\":\"Product not found: 420\",\"path\":\"uri=/carts\"}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isNotFound())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls POST "carts" but with no products*/
  @Test
  public void testAddEmptyCart() throws Exception {
    final String expectedResponse = "{\"cartId\":4,\"products\":[],\"checkedOut\":false}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
        .andExpect(status().isCreated())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls POST "carts" but with a negative product quantity*/
  @Test
  public void testAddCartNegativeQuantity() throws Exception {
    final String request =
        """
            [
                {
                    "productId": 1,
                    "quantity": 3
                },
                {
                    "productId": 2,
                    "quantity": -1
                }
            ]""";
    final String expectedResponse =
        "{\"status\":400,\"error\":\"Validation Error\",\"message\":\"quantity must be greater than 0\",\"path\":\"uri=/carts\"}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls POST "carts" but the quantity is missing*/
  @Test
  public void testAddCartEmptyQuantity() throws Exception {
    final String request =
        """
            [
                {
                    "productId": 1,
                    "quantity": 3
                },
                {
                    "productId": 2
                }
            ]""";
    final String expectedResponse =
        "{\"status\":400,\"error\":\"Validation Error\",\"message\":\"quantity must not be null\",\"path\":\"uri=/carts\"}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls POST "carts" but the productID is missing*/
  @Test
  public void testAddCartEmptyProdID() throws Exception {
    final String request =
        """
            [
                {
                    "productId": 1,
                    "quantity": 3
                },
                {
                    "quantity": -1
                }
            ]""";
    final String expectedResponse =
        "{\"status\":400,\"error\":\"Bad argument\",\"message\":\"The given id must not be null\",\"path\":\"uri=/carts\"}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls POST "carts" but the quantity is 0*/
  @Test
  public void testAddCartZeroQuantity() throws Exception {
    final String request =
        """
            [
                {
                    "productId": 1,
                    "quantity": 3
                },
                {
                    "productId": 2,
                    "quantity": 0
                }
            ]""";
    final String expectedResponse =
        "{\"status\":400,\"error\":\"Validation Error\",\"message\":\"quantity must be greater than 0\",\"path\":\"uri=/carts\"}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls POST "/carts/:id/checkout"*/
  @Test
  public void testCheckoutCart() throws Exception {
    //    productId":1,"name":"Fancy IPA Beer","price":5.99
    //    productId":3,"name":"Organic Blueberry Jam","price":4.25,
    //    Total cost = (2 * 5.99) + (1 * 4.25) = 16.23
    final String expectedResponse =
        "{\"cart\":{\"cartId\":1,\"products\":[{\"productId\":1,\"quantity\":2},{\"productId\":3,\"quantity\":1}],\"checkedOut\":true},\"totalCost\":16.23}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/carts/1/checkout")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(expectedResponse));
    //  verify the cart checkedOut = True when getting all carts
    final String expectedGETResponse =
        "[{\"cartId\":1,\"products\":[{\"productId\":1,\"quantity\":2},{\"productId\":3,\"quantity\":1}],\"checkedOut\":true},{\"cartId\":2,\"products\":[{\"productId\":2,\"quantity\":3},{\"productId\":4,\"quantity\":1}],\"checkedOut\":true},{\"cartId\":3,\"products\":[{\"productId\":5,\"quantity\":1},{\"productId\":6,\"quantity\":2}],\"checkedOut\":false}]";
    mockMvc
        .perform(MockMvcRequestBuilders.get("/carts"))
        .andExpect(status().isOk())
        .andExpect(content().string(expectedGETResponse));
  }

  /*Test that calls POST "/carts/:id/checkout" on a cart doesnt exist*/
  @Test
  public void testCheckoutCartInvalid() throws Exception {
    final String expectedResponse =
        "{\"status\":404,\"error\":\"Cart could not be found\",\"message\":\"Cart 400 not found\",\"path\":\"uri=/carts/400/checkout\"}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/carts/400/checkout")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().string(expectedResponse));
  }

  /*Test that calls POST "/carts/:id/checkout" on a cart thats already checked out*/
  @Test
  public void testCheckoutCartAlreadyCheckedOut() throws Exception {
    final String expectedResponse =
        "{\"status\":400,\"error\":\"Cart already checked out\",\"message\":\"Cart 2 is already checked out.\",\"path\":\"uri=/carts/2/checkout\"}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/carts/2/checkout")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(expectedResponse));
  }
}
