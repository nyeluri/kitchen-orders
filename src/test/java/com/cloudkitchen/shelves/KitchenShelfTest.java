package com.cloudkitchen.shelves;

import com.cloudkitchen.dtos.Order;
import com.cloudkitchen.dtos.ShelfType;
import com.cloudkitchen.dtos.Temp;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class KitchenShelfTest {

  @RunWith(Parameterized.class)
  public static class ParameterizedTests {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
      return Arrays.asList(new Object[][] {
          { Temp.HOT, 1 }, { Temp.HOT, 2}, { Temp.COLD, 1}, { Temp.FROZEN, 1 }, { Temp.HOT, 3 }, { Temp.FROZEN, 2 }, { Temp.COLD, 2 }
      });
    }

    @Parameterized.Parameter
    public Temp temp;

    @Parameterized.Parameter(1)
    public int expectedSize;

    @Test
    public void testShelfPlacementWithCapacity(){
      Order order = new Order(UUID.randomUUID().toString(), "pizza", temp,300, 0.35,null);
      KitchenShelf.placeOnBestPossibleShelf(order);
      assertEquals(ShelfFactory.getShelf(order.getTemp()).getCurrentCapacity(),expectedSize);
    }
  }

  @RunWith(PowerMockRunner.class)
  @PrepareForTest( { ShelfFactory.class })
  @PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*"})
  public static class OtherTests{

    /**
     * Tests movement of Order from Overflow shelf to least capacity shelf
     * @throws IllegalAccessException
     */
    @Test
    public void testOverflowMove() throws IllegalAccessException {
      ColdShelf coldShelf = new ColdShelf();
      coldShelf.CAPACITY = 2;
      coldShelf.getRack().put("11",new Order("11", "coke", Temp.COLD, 250, 1.2, Instant.now()));
      coldShelf.getRack().put("12",new Order("12", "milkshake", Temp.COLD, 300, 1.2, Instant.now()));

      HotShelf hotShelf = new HotShelf();
      hotShelf.CAPACITY = 2;
      hotShelf.getRack().put("21",new Order("21", "pizza", Temp.HOT, 250, 1.2, Instant.now()));

      FrozenShelf frozenShelf = new FrozenShelf();
      frozenShelf.CAPACITY = 2;
      frozenShelf.getRack().put("31",new Order("31", "cone", Temp.FROZEN, 250, 1.2, Instant.now()));
      frozenShelf.getRack().put("32",new Order("32", "icecream", Temp.FROZEN, 300, 1.2, Instant.now()));

      OverFlowShelf overFlowShelf = new OverFlowShelf();
      overFlowShelf.CAPACITY = 3;
      overFlowShelf.getRack().put("13",new Order("41", "pepsi", Temp.COLD, 250, 1.2, Instant.now()));
      overFlowShelf.getRack().put("22",new Order("22", "yellowcurry", Temp.HOT, 300, 1.2, Instant.now()));
      overFlowShelf.getRack().put("33",new Order("33", "veggies", Temp.FROZEN, 300, 1.2, Instant.now()));

      Map<Temp, Shelf> shelves = new HashMap<>();
      shelves.put(Temp.HOT, hotShelf);
      shelves.put(Temp.COLD, coldShelf);
      shelves.put(Temp.FROZEN, frozenShelf);
      shelves.put(null, overFlowShelf);

      PowerMockito.mockStatic(ShelfFactory.class);
      Mockito.when(ShelfFactory.getShelf(Temp.HOT)).thenReturn(hotShelf);
      Mockito.when(ShelfFactory.getShelf(Temp.COLD)).thenReturn(coldShelf);
      Mockito.when(ShelfFactory.getShelf(Temp.FROZEN)).thenReturn(frozenShelf);
      Mockito.when(ShelfFactory.getShelf(null)).thenReturn(overFlowShelf);
      Mockito.when(ShelfFactory.getShelvesMap()).thenReturn(shelves);
      Mockito.when(ShelfFactory.getLeastCapacityShelf(Mockito.any(Temp.class))).thenCallRealMethod();

      Order order = new Order("14", "7up", Temp.COLD, 250, 1.2, Instant.now());
      KitchenShelf.placeOnBestPossibleShelf(order);
      assertEquals(ShelfFactory.getShelf(null).getCurrentCapacity(),3);
      assertEquals(ShelfFactory.getShelf(Temp.HOT).getCurrentCapacity(),2);
      assertEquals(ShelfType.OVERFLOW_SHELF, KitchenShelf.pickOrder(order));
      assertEquals(ShelfType.HOT_SHELF, KitchenShelf.pickOrder(
          new Order("22", "yellowcurry", Temp.HOT, 300, 1.2, Instant.now())));
    }

    /**
     * Tests removal of Order from Overflow shelf
     * @throws IllegalAccessException
     */
    @Test
    public void testOverflowRemoval() throws IllegalAccessException {
      ColdShelf coldShelf = new ColdShelf();
      coldShelf.CAPACITY = 2;
      coldShelf.getRack().put("11",new Order("11", "coke", Temp.COLD, 250, 1.2, Instant.now()));
      coldShelf.getRack().put("12",new Order("12", "milkshake", Temp.COLD, 300, 1.2, Instant.now()));

      HotShelf hotShelf = new HotShelf();
      hotShelf.CAPACITY = 2;
      hotShelf.getRack().put("21",new Order("21", "pizza", Temp.HOT, 250, 1.2, Instant.now()));
      hotShelf.getRack().put("23",new Order("23", "soup", Temp.HOT, 250, 1.2, Instant.now()));

      FrozenShelf frozenShelf = new FrozenShelf();
      frozenShelf.CAPACITY = 2;
      frozenShelf.getRack().put("31",new Order("31", "cone", Temp.FROZEN, 250, 1.2, Instant.now()));
      frozenShelf.getRack().put("32",new Order("32", "icecream", Temp.FROZEN, 300, 1.2, Instant.now()));

      OverFlowShelf overFlowShelf = new OverFlowShelf();
      overFlowShelf.CAPACITY = 3;
      overFlowShelf.getRack().put("13",new Order("41", "pepsi", Temp.COLD, 300, 1.2, Instant.now()));
      overFlowShelf.getRack().put("22",new Order("22", "yellowcurry", Temp.HOT, 300, 1.2, Instant.now()));
      overFlowShelf.getRack().put("33",new Order("33", "veggies", Temp.FROZEN, 300, 1.2, Instant.now()));

      Map<Temp, Shelf> shelves = new HashMap<>();
      shelves.put(Temp.HOT, hotShelf);
      shelves.put(Temp.COLD, coldShelf);
      shelves.put(Temp.FROZEN, frozenShelf);
      shelves.put(null, overFlowShelf);

      PowerMockito.mockStatic(ShelfFactory.class);
      Mockito.when(ShelfFactory.getShelf(Temp.HOT)).thenReturn(hotShelf);
      Mockito.when(ShelfFactory.getShelf(Temp.COLD)).thenReturn(coldShelf);
      Mockito.when(ShelfFactory.getShelf(Temp.FROZEN)).thenReturn(frozenShelf);
      Mockito.when(ShelfFactory.getShelf(null)).thenReturn(overFlowShelf);
      Mockito.when(ShelfFactory.getShelvesMap()).thenReturn(shelves);
      Mockito.when(ShelfFactory.getLeastCapacityShelf(Mockito.any(Temp.class))).thenCallRealMethod();

      Order order = new Order("14", "7up", Temp.COLD, 250, 1.2, Instant.now());
      KitchenShelf.placeOnBestPossibleShelf(order);
      assertEquals(ShelfFactory.getShelf(null).getCurrentCapacity(),3);
      assertEquals(ShelfFactory.getShelf(Temp.HOT).getCurrentCapacity(),2);
      assertEquals(ShelfType.OVERFLOW_SHELF, KitchenShelf.pickOrder(order));
      assertEquals(null, KitchenShelf.pickOrder(
          new Order("22", "yellowcurry", Temp.HOT, 300, 1.2, Instant.now())));
    }
  }
}