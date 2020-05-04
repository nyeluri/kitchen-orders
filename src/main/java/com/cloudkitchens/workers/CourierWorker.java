package com.cloudkitchens.workers;

import com.cloudkitchens.KitchenShelf;
import com.cloudkitchens.dtos.Order;
import com.cloudkitchens.dtos.Shelf;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CourierWorker implements Runnable {
  private Order order;
  public CourierWorker(Order order) {
    this.order = order;
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used
   * to create a thread, starting the thread causes the object's
   * <code>run</code> method to be called in that separately executing
   * thread.
   * <p>
   * The general contract of the method <code>run</code> is that it may
   * take any action whatsoever.
   *
   * @see Thread#run()
   */
  @Override
  public void run() {
    Shelf shelf = KitchenShelf.pickOrder(order.getId(), order.getTemp());
    if(shelf != null) {
      if(order.getOrderValue(shelf) > 0){
        pickOrder(shelf);
        deliverOrder();
      } else {
        log.info("{} discarded during pickup", this.order.getId());
      }
    } else {
      log.warn("{} not available on shelf",order.getId());
    }
  }

  private void pickOrder(Shelf shelf) {
    log.info("{} picked from {} shelf by Courier", this.order.getId(), shelf);
  }

  private void deliverOrder() {
    log.info("{} delivered by courier", this.order.getId());
  }

  private boolean sanityCheckOrder() {
    return false;
  }
}
