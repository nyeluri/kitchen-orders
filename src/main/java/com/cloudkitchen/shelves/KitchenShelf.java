package com.cloudkitchen.shelves;

import com.cloudkitchen.dtos.Order;
import com.cloudkitchen.dtos.ShelfType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
/**
 * KitchenShelf is a simple class that holds
 * Shelves Data structures and utility methods around them.
 */
public class KitchenShelf {

  /**
   * Places an order on best possible shelf
   * Rule 1 - Place the order on the shelf with Temp type same as Order
   * Rule 2 - If above shelf doesnt have enough capacity,
   * then will place it on overflow shelf.
   * @param order - Order to be placed into shelf
   */
  public static void placeOnBestPossibleShelf(final Order order) {
    if (ShelfFactory.getShelf(order.getTemp()).placeOnShelf(order)) {
      log.info("{} placed on {} Shelf", order.getId(), order.getTemp());
    } else {
      log.info("{} no place on {} Shelf", order.getId(), order.getTemp());
      ShelfFactory.getShelf(null).placeOnShelf(order);
    }
  }

  /**
   * Picks the order from shelf with temp type same as current order.
   * Else will try to pick it from overflow shelf.
   * @param order
   * @return ShelfType - ShelfType Enum values if the order is present else null
   */
  public static ShelfType pickOrder(Order order) {
    Shelf shelf = ShelfFactory.getShelf(order.getTemp());
    Shelf overFlowShelf = ShelfFactory.getShelf(null);
    if (shelf.removeFromShelf(order)) {
      return shelf.getShelfType();
    } else if(overFlowShelf.removeFromShelf(order)){
        return ShelfType.OVERFLOW_SHELF;
    }
    return null;
  }
}
