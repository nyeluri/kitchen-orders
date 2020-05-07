package com.cloudkitchen.workers;

import com.cloudkitchen.CloudKitchen;
import com.cloudkitchen.shelves.KitchenShelf;
import com.cloudkitchen.dtos.Order;
import lombok.extern.slf4j.Slf4j;
import java.time.Instant;

@Slf4j
/**
 * Simple class modelling a Kitchen with methods to cook and place on shelf.
 */
public class KitchenWorker{

  /**
   * Cooks the order Instantly and places it on a shelf
   */
  public static void processOrder(Order order) {
    // 1. Cook Order
    cookOrder(order);
    CloudKitchen.cookedCount.getAndIncrement();

    // 2. Place it on Best possible shelf
    KitchenShelf.placeOnBestPossibleShelf(order);
  }

  /**
   * Cooks the order Instantly.
   */
  private static void cookOrder(Order order) {
    log.info("{} order cooked", order.getId());
    order.setPreparedTime(Instant.now());
  }
}
