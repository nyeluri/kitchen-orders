package com.cloudkitchens.dtos;

import lombok.Data;
import java.time.Duration;
import java.time.Instant;

@Data
/**
 * Order DTO.
 */
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
  /**
   * Get Order Value.
   * @param shelf - Shelf Type.
   * @return Order value
   */
  public double getOrderValue(Shelf shelf) {
    int decayModifier = 0;
    switch(shelf) {
      case OVERFLOW_SHELF:
        decayModifier =  2;
        break;
      default:
        decayModifier = 1;
        break;
    }
    return (shelfLife - decayRate * Duration.between(Instant.now(),
        preparedTime).getSeconds() * decayModifier) / shelfLife;
  }
}
