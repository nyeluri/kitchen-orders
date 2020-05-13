package com.cloudkitchen.shelves;

import com.cloudkitchen.dtos.Order;
import com.cloudkitchen.dtos.ShelfType;
import com.cloudkitchen.dtos.Temp;
import com.cloudkitchen.CloudKitchen;
import lombok.extern.slf4j.Slf4j;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
/**
 * Class extending Shelf, but defines methods specific to overflow shelf
 */
public class OverFlowShelf extends Shelf {

  // Initialize shelf capacity & type
  {
    CAPACITY = 15;
    shelfType = ShelfType.OVERFLOW_SHELF;
    rack = new ConcurrentHashMap<>(CAPACITY);
  }

  @Override
  /**
   * Places the order on overflow shelf if it has enough capacity.
   * Else it will try to move or discard another order from overflow
   * shelf and places current order.
   * @param order - Order.
   * @return boolean - always return true.
   */
  public boolean placeOnShelf(Order order) {
    if(!hasCapacity()) {
      log.info("{} no place on OVERFLOW Shelf", order.getId());
      moveOrDiscardOrder(order.getTemp());
    }
    this.rack.put(order.getId(), order);
    CloudKitchen.overFlowPlacedCount.getAndIncrement();
    log.info("{} placed on OVERFLOW Shelf", order.getId());
    return true;
  }

  /**
   * Find an order to move or discard from overflow shelf
   *
   * First it will try to move an order with temp type different from provided one.
   * In doing so, it will look shelf with Temp type different from provided one
   * and with least current capacity. Then depending on picked shelf temp type
   * it will choose an item from overflow shelf with low order value and moves it
   * to the picked shelf.
   *
   * Else it will remove an order from overflow shelf with least value.
   * @param temp - Temp type of the order to be added to overflow shelf
   */
  private void moveOrDiscardOrder(Temp temp) {
    Optional<Order> optionalOrder = null;
    // 1. Get Least capacity shelf with temp type other current one
    Optional<Shelf> leastCapacityShelf = ShelfFactory.getLeastCapacityShelf(temp);

    if(leastCapacityShelf.isPresent()) {
      // 2. Move Item (with temp same as least capacity shelf) of lowest life from overflow into  shelf with capacity
      optionalOrder = this.rack.values().stream()
          .filter(o -> o.getTemp().equals(leastCapacityShelf.get().getShelfType().getTemp()))
          .min(Comparator.comparing(o -> o.getOrderValue(ShelfType.OVERFLOW_SHELF)));
      if(optionalOrder.isPresent()) {
        removeFromShelf(optionalOrder.get());
        CloudKitchen.overFlowMovedCount.getAndIncrement();
        leastCapacityShelf.get().placeOnShelf(optionalOrder.get());
        log.info("{} moved from Overflow to {}", optionalOrder.get().getId(), leastCapacityShelf.get().getShelfType());
      } else {
        // 3. Remove an order with least shelf life
        Optional<Order> leastLifeOrder = getOrderWithLeastValue();
        if (leastLifeOrder.isPresent()) {
          log.info("{} removed from overflow shelf due to insufficient place", leastLifeOrder.get().getId());
          removeFromShelf(leastLifeOrder.get());
          CloudKitchen.overFlowRemovedCount.getAndIncrement();
        }
      }
    } else {
      // 4. Remove an order with least shelf life
      Optional<Order> leastLifeOrder = getOrderWithLeastValue();
      if (leastLifeOrder.isPresent()) {
        log.info("{} removed from overflow shelf due to insufficient place", leastLifeOrder.get().getId());
        removeFromShelf(leastLifeOrder.get());
        CloudKitchen.overFlowRemovedCount.getAndIncrement();
      }
    }
  }
}
