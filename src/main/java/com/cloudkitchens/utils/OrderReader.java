package com.cloudkitchens.utils;

import com.cloudkitchens.dtos.Order;
import java.io.File;
import java.io.IOException;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Hi.
 * Class foor I dont know
 */
public class OrderReader {
  /** Hi.
   * Returns List of Orders
   * @return
   */
  public List<Order> readOrders() {
    List<Order> orders = null;
    try  {
      ObjectMapper mapper = new ObjectMapper();
      orders = mapper.readValue(
          new File(getClass().getClassLoader().getResource("orders.json").getFile()),
          mapper.getTypeFactory().constructCollectionType(List.class, Order.class));
    } catch (IOException e2) {
      e2.printStackTrace();
    }
    return orders;
  }
}
