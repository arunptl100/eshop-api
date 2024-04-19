package com.eshopapi.eshopapi.controller.cart;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ConcurrencyIT {

  @Autowired private MockMvc mockMvc;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Test
  public void testConcurrentRequests() throws Exception {
    final int cartId = 1;

    final String request =
        """
                    [
                        {
                            "productId": 1,
                            "quantity": 3
                        }
                    ]""";

    final String request2 =
        """
                    [
                        {
                            "productId": 2,
                            "quantity": 2
                        }
                    ]""";
    ExecutorService service = Executors.newFixedThreadPool(2);
    Future<MvcResult> future1 =
        service.submit(
            () ->
                mockMvc
                    .perform(
                        MockMvcRequestBuilders.put("/carts/" + cartId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andReturn());

    Future<MvcResult> future2 =
        service.submit(
            () ->
                mockMvc
                    .perform(
                        MockMvcRequestBuilders.put("/carts/" + cartId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request2))
                    .andReturn());

    // Wait for the results
    MvcResult result1 = future1.get();
    MvcResult result2 = future2.get();

    // Both requests should succeed a
    assertEquals(200, result1.getResponse().getStatus());
    assertEquals(200, result2.getResponse().getStatus());
  }
}
