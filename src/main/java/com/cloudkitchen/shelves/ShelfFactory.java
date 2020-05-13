package com.cloudkitchen.shelves;

import com.cloudkitchen.dtos.ShelfType;
import com.cloudkitchen.dtos.Temp;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ShelfFactory class provides 2 basic methods to retrieve a shelf by Temp / Capacity
 */
public class ShelfFactory {

  /**
   * Returns a map with all shelves
   * @return - Map with Temp type and corresponding Shelf
   */
  public static Map<Temp, Shelf> getShelvesMap() {
    return shelvesMap;
  }

  private static Map<Temp, Shelf> shelvesMap = new HashMap<>();

  // Initialize  Map with all Shelves
  static {
    shelvesMap.put(Temp.HOT, new HotShelf());
    shelvesMap.put(Temp.COLD, new ColdShelf());
    shelvesMap.put(Temp.FROZEN, new FrozenShelf());
    shelvesMap.put(null, new OverFlowShelf());
  }

  /**
   * A Simple factory class that returns an instance of Shelf according to temperature
   * @param temp - Temperature Type.
   * @return Shelf - Shelf Instance
   */
  public static Shelf getShelf(Temp temp) {
    return shelvesMap.get(temp);
  }

  /**
   * Identifies a shelf with least current capacity
   * @param temp - Temperature Type.
   * @return Shelf - Optional Shelf
   */
  public static Optional<Shelf> getLeastCapacityShelf(Temp temp) {
    Optional<Shelf> shelf = ShelfFactory.getShelvesMap().values().stream().filter(s -> !(ShelfType.OVERFLOW_SHELF.equals(s.getShelfType())
        || ShelfType.getShelfTypeByTemp(temp).equals(s.getShelfType())) && s.hasCapacity())
        .min(Comparator.comparing(s -> s.getCurrentCapacity()));
    return shelf;
  }
}
