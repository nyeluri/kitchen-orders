package com.cloudkitchen.workers;

import com.cloudkitchen.CloudKitchen;
import com.cloudkitchen.dtos.Order;
import lombok.extern.slf4j.Slf4j;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
/**
 * Simple class that models a receptionist who takes orders and notifies Kitchen & Courier.
 */
public class ReceptionistWorker implements Runnable {

  /**
   * Order to be processed.
   */
  private Order order;

  /**
   * Computes a result, or throws an exception if unable to do so.
   * @return computed result
   * @throws Exception if unable to compute a result
   */
  @Override
  public void run() {
    try {
      for(int i = 0; i< CloudKitchen.ingestionRate; i++){
        // 1. Receive an order
        order = CloudKitchen.ordersToBeProcessed.remove();
        log.info("{} order received. {} more to process",order.getId(), CloudKitchen.ordersToBeProcessed.size());
        CloudKitchen.receivedCount.getAndIncrement();

        // 2. Request for courier pickup who arrives in 2 - 6 seconds
        CloudKitchen.courierScheduler.schedule
            (new CourierWorker(order), getRandomNoInRange(2,6), TimeUnit.SECONDS);

        // 3. Send it Kitchen
        KitchenWorker.processOrder(order);
      }
    } catch(NoSuchElementException e) {
      log.warn("No more orders to be processed, notifying CSSSystem");
      synchronized (CloudKitchen.class) {
        CloudKitchen.class.notify();
      }
    } catch(Exception e){
      e.printStackTrace();
    }
  }

  /**
   * Simple utility method to return random ints within a range.
   * @param min - Minimum range.
   * @param max - Maximum range.
   * @return - Random integers between minimum and maximum range.
   */
  private static int getRandomNoInRange(int min, int max) {
    Random r = new Random();
    return r.nextInt((max - min) + 1) + min;
  }
}
