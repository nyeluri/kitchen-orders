package com.cloudkitchen.shelves;

import com.cloudkitchen.dtos.Order;
import com.cloudkitchen.dtos.ShelfType;
import lombok.Data;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

@Data
/**
 * Abstract Class defining most common methods for shelves
 */
public abstract class Shelf {

  /**
   * Shelf Capacity.
   */
  public int CAPACITY = 10;

  /**
   * Rack for orders.
   */
  public Map<String, Order> rack;

  /**
   * Shelf Type.
   */
  public ShelfType shelfType;

  /**
   * Places the order on shelf if it has enough capacity.
   * @param order - Order.
   * @return boolean - true if order is placed into the shelf else false.
   */
  public abstract boolean placeOnShelf(Order order);

  /**
   * Validates if shelf has enough capacity.
   * @return boolean - true if shelf rack has enough capacity else false.
   */
  public boolean hasCapacity() {
    return this.rack.size() < CAPACITY;
  }

  /**
   * Removes the order from shelf.
   * @param order - Order.
   * @return boolean - true if order available else false.
   */
  public boolean removeFromShelf(Order order){
    return this.rack.remove(order.getId())!=null;
  }

  /**
   * Returns current shelf capacity.
   * @return boolean - no of orders in the shelf
   */
  public int getCurrentCapacity(){
    return this.rack.size();
  }

  /**
   * Finds the order with least value
   * @return order - Order with least value
   */
  public Optional<Order> getOrderWithLeastValue() {
    return this.rack.values().stream()
        .min(Comparator.comparing(o -> o.getOrderValue(this.shelfType)));
  }
}
