package com.cloudkitchen.workers;

import com.cloudkitchen.CloudKitchen;
import com.cloudkitchen.shelves.KitchenShelf;
import com.cloudkitchen.dtos.Order;
import com.cloudkitchen.dtos.ShelfType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * Simple class modelling a Courier and implements a Runnable to simulate multiple courier workers.
 */
public class CourierWorker implements Runnable {
  private Order order;
  public CourierWorker(Order order) {
    this.order = order;
  }

  /**
   * implementation of Runnable run method
   * It tries to pick the order from Kitchen Shelf, discards the
   * order if it is stale, else delivers it instantly.
   * @see Thread#run()
   */
  @Override
  public void run() {
    ShelfType shelf = KitchenShelf.pickOrder(order);
    if(shelf != null) {
      log.info("{} picked from {} shelf by Courier", this.order.getId(), shelf);
      CloudKitchen.pickedCount.getAndIncrement();
      if(order.getOrderValue(shelf) > 0){
        deliverOrder();
        CloudKitchen.deliveredCount.getAndIncrement();
      } else {
        log.info("{} decayed during pickup", this.order.getId());
        CloudKitchen.decayedCount.getAndIncrement();
      }
    } else {
      log.warn("{} not available on shelf",order.getId());
    }
  }

  /**
   * Delivers an order instantly.
   */
  private void deliverOrder() {
    log.info("{} delivered by courier", this.order.getId());
  }
}
