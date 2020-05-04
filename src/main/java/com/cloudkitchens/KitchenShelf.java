package com.cloudkitchens;

import com.cloudkitchens.dtos.Order;
import com.cloudkitchens.dtos.Shelf;
import com.cloudkitchens.dtos.Temp;
import lombok.extern.slf4j.Slf4j;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * KitchenShelf is a simple class that holds
 * Shelves Data structures and utility methods around them.
 */
@Slf4j
public class KitchenShelf {
  private static Map<String, Order> hotShelf = Collections.synchronizedMap(new HashMap<>(10));
  private static Map<String, Order> coldShelf = Collections.synchronizedMap(new HashMap<>(10));
  private static Map<String, Order> frozenShelf = Collections.synchronizedMap(new HashMap<>(10));
  private static Map<String, Order> overFlowShelf = Collections.synchronizedMap(new LinkedHashMap<>(15));

  /**
   * Cooks the order Instantly.
   */
  public static void placeOnBestPossibleShelf(final Order order) {
    switch(order.getTemp()){
      case COLD:
        if(coldShelf.size() < 10){
          coldShelf.put(order.getId(), order);
          log.info("{} placed on {} Shelf", order.getId(), order.getTemp());
        } else {
          log.info("{} no place on {} Shelf", order.getId(), order.getTemp());
          putOnOverFlowShelf(order);
        }
        break;
      case HOT:
        if(hotShelf.size() < 10){
          hotShelf.put(order.getId(), order);
          log.info("{} placed on {} Shelf", order.getId(), order.getTemp());
        } else {
          log.info("{} no place on {} Shelf", order.getId(), order.getTemp());
          putOnOverFlowShelf(order);
        }
        break;
      case FROZEN:
        if(frozenShelf.size() < 10){
          frozenShelf.put(order.getId(), order);
          log.info("{} placed on {} Shelf", order.getId(), order.getTemp());
        } else {
          log.info("{} no place on {} Shelf", order.getId(), order.getTemp());
          putOnOverFlowShelf(order);
        }
        break;
    }
  }

  /**
   * Put on overflow shelf
   */
  private static void putOnOverFlowShelf(Order order){
    if(overFlowShelf.size() < 15){
      overFlowShelf.put(order.getId(), order);
    } else {
      log.info("{} no place on overflow Shelf", order.getId());
      moveOrDiscardOrder(order.getTemp());
      overFlowShelf.put(order.getId(), order);
    }
    log.info("{} placed on overflow Shelf", order.getId());
  }

  /**
   * Find an order to Move.
   */
  private static void moveOrDiscardOrder(Temp temp) {
    Optional<Order> optionalOrder = null;
    switch (temp) {
      case COLD:
        // Move the Oldest Hot Order from Overflow to Hot Shelf
        if(hotShelf.size() < 10 && (optionalOrder = overFlowShelf.values().stream()
            .filter(o -> o.getTemp().equals(Temp.HOT)).findFirst()).isPresent()){
          Order order = optionalOrder.get();
          overFlowShelf.remove(order.getId());
          hotShelf.put(order.getId(), order);
          log.info("{} moved from overflow shelf to hot shelf", order.getId());
        } else if(frozenShelf.size() < 10 && (optionalOrder = overFlowShelf.values().stream()
            .filter(o -> o.getTemp().equals(Temp.FROZEN)).findFirst()).isPresent()){
          Order order = optionalOrder.get();
          overFlowShelf.remove(order.getId());
          frozenShelf.put(order.getId(), order);
          log.info("{} moved from overflow shelf to frozen shelf", order.getId());
        } else {
          discardOverFlowOrder();
        }
        break;
      case HOT:
        // Move the Oldest Hot Order from Overflow to Hot Shelf
        if(coldShelf.size() < 10 && (optionalOrder = overFlowShelf.values().stream()
            .filter(o -> o.getTemp().equals(Temp.COLD)).findFirst()).isPresent()){
          Order order = optionalOrder.get();
          overFlowShelf.remove(order.getId());
          coldShelf.put(order.getId(), order);
          log.info("{} moved from overflow shelf to cold shelf", order.getId());
        } else if(frozenShelf.size() < 10 && (optionalOrder = overFlowShelf.values().stream()
            .filter(o -> o.getTemp().equals(Temp.FROZEN)).findFirst()).isPresent()){
          Order order = optionalOrder.get();
          overFlowShelf.remove(order.getId());
          frozenShelf.put(order.getId(), order);
          log.info("{} moved from overflow shelf to frozen shelf", order.getId());
        } else {
          discardOverFlowOrder();
        }
        break;
      case FROZEN:
        // Move the Oldest Hot Order from Overflow to Hot Shelf
        if(hotShelf.size() < 10 && (optionalOrder = overFlowShelf.values().stream()
            .filter(o -> o.getTemp().equals(Temp.HOT)).findFirst()).isPresent()){
          Order order = optionalOrder.get();
          overFlowShelf.remove(order.getId());
          coldShelf.put(order.getId(), order);
          log.info("{} moved from overflow shelf to hot shelf", order.getId());
        } else if(coldShelf.size() < 10 && (optionalOrder = overFlowShelf.values().stream()
            .filter(o -> o.getTemp().equals(Temp.COLD)).findFirst()).isPresent()){
          Order order = optionalOrder.get();
          overFlowShelf.remove(order.getId());
          coldShelf.put(order.getId(), order);
          log.info("{} moved from overflow shelf to cold shelf", order.getId());
        } else {
          discardOverFlowOrder();
        }
        break;
    }
  }

  /**
   * Find an order within Overflow shelf to discard
   */
  private static void discardOverFlowOrder() {
    Optional<Order> discardOrder = overFlowShelf.values().stream()
        .min(Comparator.comparing(o -> o.getOrderValue(Shelf.OVERFLOW_SHELF)));
    if (discardOrder.isPresent()) {
      log.info("{} Discarded from overflow shelf due to insufficient place", discardOrder.get().getId());
    }
  }

  /**
   * Pick an order from Shelves
   */
  public static Shelf pickOrder(String orderId, Temp temp) {
    Order pickupOrder = null;
    Shelf shelf = null;
    if(Temp.COLD.equals(temp)){
      synchronized (coldShelf) {
        pickupOrder = coldShelf.remove(orderId);
        shelf = Shelf.COLD_SHELF;
      }
    } else if(Temp.HOT.equals(temp)){
      synchronized (hotShelf) {
        pickupOrder = hotShelf.get(orderId);
        shelf = Shelf.COLD_SHELF;
      }
    } else if(Temp.FROZEN.equals(temp)){
      synchronized (frozenShelf) {
        pickupOrder = frozenShelf.get(orderId);
        shelf = Shelf.COLD_SHELF;
      }
    }
    if(pickupOrder != null) {
      shelf = Shelf.COLD_SHELF;
    } else {
      if(overFlowShelf.containsKey(orderId)) {
        pickupOrder = overFlowShelf.get(orderId);
      }
    }
    return shelf;
  }
}
