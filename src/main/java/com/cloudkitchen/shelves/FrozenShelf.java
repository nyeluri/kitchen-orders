package com.cloudkitchen.shelves;

import com.cloudkitchen.dtos.Order;
import com.cloudkitchen.dtos.ShelfType;
import com.cloudkitchen.CloudKitchen;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class extending Shelf, but defines methods specific to Frozen shelf
 */
public class FrozenShelf extends Shelf {

  {
    shelfType = ShelfType.FROZEN_SHELF;
    rack = new ConcurrentHashMap<>(CAPACITY);
  }

  @Override
  /**
   * Places the order on shelf if it has enough capacity.
   * @param order - Order.
   * @return boolean - true if order is placed into the shelf else false.
   */
  public boolean placeOnShelf(Order order) {
    if(hasCapacity()) {
      rack.put(order.getId(), order);
      CloudKitchen.frozenPlacedCount.getAndIncrement();
      return true;
    }
    return false;
  }
}
