package com.eshopapi.eshopapi.controller.cart;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eshopapi.eshopapi.EshopApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = EshopApiApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GETCartControllerIT {
  private final String initial_db_carts =
      "[{\"cartId\":1,\"products\":[{\"productId\":1,\"quantity\":2},{\"productId\":3,\"quantity\":1}],\"checkedOut\":false},{\"cartId\":2,\"products\":[{\"productId\":2,\"quantity\":3},{\"productId\":4,\"quantity\":1}],\"checkedOut\":true},{\"cartId\":3,\"products\":[{\"productId\":5,\"quantity\":1},{\"productId\":6,\"quantity\":2}],\"checkedOut\":false}]";
  @Autowired private MockMvc mockMvc;

  /*Test that calls GET "carts" */
  @Test
  public void testGetAllCarts() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/carts"))
        .andExpect(status().isOk())
        .andExpect(content().string(initial_db_carts));
  }
}
