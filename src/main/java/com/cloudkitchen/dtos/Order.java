package com.cloudkitchen.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Duration;
import java.time.Instant;


/**
 * Order DTO.
 */
@Data
@AllArgsConstructor
public class Order {
  /**
   * Unique Id of the Order.
   */
  private String id;
  /**
   * Name of the Order.
   */
  private String name;
  /**
   * Temperature requirement.
   */
  private Temp temp;
  /**
   * Maximum Shelf life of the order.
   */
  private long shelfLife;
  /**
   * Decay rate of the order.
   */
  private double decayRate;
  /**
   * Prepared Time.
   */
  private Instant preparedTime;

  public Order(){

  }
  /**
   * Get Order Value.
   * @param shelf - Shelf Type.
   * @return Order value
   */
  public double getOrderValue(ShelfType shelf) {
    return (shelfLife - decayRate * Duration.between(Instant.now(),
        preparedTime).getSeconds() * shelf.getDecayModifier()) / shelfLife;
  }
}
