package com.cloudkitchens.workers;

import com.cloudkitchens.KitchenShelf;
import com.cloudkitchens.dtos.Order;
import lombok.extern.slf4j.Slf4j;
import java.time.Instant;
/**
 * KitchenWorker is Callable Implementation which instantly
 * cooks orders and places it on best possible shelf.
 */
@Slf4j
public class KitchenWorker{

  public static void processOrder(Order order) {
    // 1. Cook Order
    cookOrder(order);

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
