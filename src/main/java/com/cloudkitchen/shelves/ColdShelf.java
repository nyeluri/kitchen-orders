package com.cloudkitchen.shelves;

import com.cloudkitchen.dtos.Order;
import com.cloudkitchen.dtos.ShelfType;
import com.cloudkitchen.CloudKitchen;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class extending Shelf, but defines methods specific to Cold shelf
 */
public class ColdShelf extends Shelf {

  {
    shelfType = ShelfType.COLD_SHELF;
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
      CloudKitchen.coldPlacedCount.getAndIncrement();
      return true;
    }
    return false;
  }
}