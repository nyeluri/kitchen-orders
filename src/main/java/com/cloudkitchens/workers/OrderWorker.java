package com.cloudkitchens.workers;

import com.cloudkitchens.CSSSystem;
import com.cloudkitchens.KitchenShelf;
import com.cloudkitchens.dtos.Order;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * KitchenWorker is Callable Implementation which instantly
 * cooks orders and places it on best possible shelf.
 */
@Slf4j
public class OrderWorker implements Runnable {

  /**
   * Order Iterator to be processed.
   */
  private Order order;

  /**
   * Computes a result, or throws an exception if unable to do so.
   *
   * @return computed result
   * @throws Exception if unable to compute a result
   */
  @Override
  public void run() {
    try {
      // 1. Receive an order
      order = CSSSystem.ordersToBeProcessed.remove();
      log.info("{} order received");

      // 2. Request for courier pickup who arrives in 2 - 6 seconds
      ScheduledExecutorService courierScheduler = Executors.newSingleThreadScheduledExecutor();
      courierScheduler.schedule(new CourierWorker(order), getRandomNoInRange(2,6), TimeUnit.MILLISECONDS);

      // 3. Send it Kitchen
      KitchenWorker.processOrder(order);
    } catch(NoSuchElementException e) {
      log.warn("No more orders to be processed, notifying CSSSystem");
      synchronized (CSSSystem.class) {
        CSSSystem.isOrdersProcessed = true;
        CSSSystem.class.notify();
      }
    }
  }

  private static int getRandomNoInRange(int min, int max){
    Random r = new Random();
    return r.nextInt((max - min) + 1) + min;
  }

  /**
   * Cooks the order Instantly.
   */
  private void cookOrder() {
    log.info("{} order cooked", order.getId());
    order.setPreparedTime(Instant.now());
  }
}
