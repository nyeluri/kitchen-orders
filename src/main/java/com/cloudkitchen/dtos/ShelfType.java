package com.cloudkitchen.dtos;

/**
 * Enum holding different Shelf types and corresponding Temp
 */
public enum ShelfType {

  FROZEN_SHELF(Temp.FROZEN, 1),
  COLD_SHELF(Temp.COLD, 1),
  HOT_SHELF(Temp.HOT, 1),
  OVERFLOW_SHELF(null, 2);

  /**
   * Temp type.
   */
  private final Temp temp;

  /**
   * DecayModifier for each Shelf.
   */
  private final int decayModifier;

  ShelfType(Temp temp, int decayModifier){
    this.temp = temp;
    this.decayModifier = decayModifier;
  }

  /**
   * Returns Shelf Type based on Temp Type
   * @param temp
   * @return ShelfType by Temp else null
   */
  public static ShelfType getShelfTypeByTemp(Temp temp) {
    for (ShelfType s : ShelfType.values()) {
      if (s.temp.equals(temp)) {
        return s;
      }
    }
    return null;
  }

  /**
   * Returns Temp type of each Shelf
   * @return Temp enum values
   */
  public Temp getTemp() {
    return this.temp;
  }

  /**
   * Returns decay modifier of each Shelf
   * @return 2 for Overflow shelf, 1 for rest of all shelves
   */
  public int getDecayModifier() {
    return this.decayModifier;
  }
}
