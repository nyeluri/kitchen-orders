package com.cloudkitchen.utils;

import com.cloudkitchen.dtos.Order;
import java.io.File;
import java.io.IOException;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility Class to read orders from .json file and deserialize them into a List
 */
public class OrderReader {
  private static final String FILE = "/orders.json";

  /**
   * Reads orders from JSON file and deserialize them into a List
   * @return - List<Order> or null
   */
  public List<Order> readOrders() {
    List<Order> orders = null;
    try  {
      ObjectMapper mapper = new ObjectMapper();
      orders = mapper.readValue(new File("orders.json"),
          mapper.getTypeFactory().constructCollectionType(List.class, Order.class));
    } catch (IOException e2) {
      e2.printStackTrace();
    }
    return orders;
  }
}
