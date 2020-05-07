package com.cloudkitchen;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import com.cloudkitchen.dtos.Order;
import com.cloudkitchen.utils.OrderReader;
import com.cloudkitchen.workers.ReceptionistWorker;

/**
 * Starter Class of CloudKitchen App.
 */
@Slf4j
public class CloudKitchen {

    /**
     * Orders to be processed.
     */
    public static BlockingQueue<Order> ordersToBeProcessed = new LinkedBlockingQueue<>();
    /**
     * Ingestion rate (orders per second)
     */
    public static int ingestionRate;
    /**
     * Thread pool for order workers.
     */
    public static ScheduledExecutorService orderScheduler;
    /**
     * Thread pool for courier workers.
     */
    public static ScheduledExecutorService courierScheduler;

    /**
     * Purely for summary purpose
     */
    public static AtomicInteger receivedCount = new AtomicInteger(0);
    public static AtomicInteger cookedCount = new AtomicInteger(0);
    public static AtomicInteger coldPlacedCount = new AtomicInteger(0);
    public static AtomicInteger hotPlacedCount = new AtomicInteger(0);
    public static AtomicInteger frozenPlacedCount = new AtomicInteger(0);
    public static AtomicInteger overFlowPlacedCount = new AtomicInteger(0);
    public static AtomicInteger overFlowRemovedCount = new AtomicInteger(0);
    public static AtomicInteger overFlowMovedCount = new AtomicInteger(0);
    public static AtomicInteger decayedCount = new AtomicInteger(0);
    public static AtomicInteger pickedCount = new AtomicInteger(0);
    public static AtomicInteger deliveredCount = new AtomicInteger(0);

    /**
     * CloudKitchen Main method
     * @param args - Order Ingestion rate (No of orders per sec format)
     */
    public static void main(final String[] args) {
        // 1. Validate Args
        if (args != null && args.length == 1) {
            try{
                ingestionRate = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                log.error("Invalid format of Ingestion Rate");
                System.exit(1);
            }
        } else {
            log.error("Invalid No of Arguements.Kitchen-Orders expects Order Ingestion rate");
            return;
        }

        // 2. Read Orders from orders.json file
        ordersToBeProcessed.addAll(new OrderReader().readOrders());

        // 3. Send Orders to Kitchen
        orderScheduler = Executors.newScheduledThreadPool(ingestionRate);
        courierScheduler = Executors.newScheduledThreadPool(ingestionRate);
        orderScheduler.scheduleAtFixedRate(new ReceptionistWorker(),
                    0, 1000, TimeUnit.MILLISECONDS);

        try{
            // 4. Wait for all orders to be processed
            synchronized (CloudKitchen.class) {
                log.warn("Waiting for all orders to be processed by kitchen");
                CloudKitchen.class.wait();
            }

            //5. Shutdown order/courier workers
            log.warn("All order/courier workers submitted. Attempting to shutdown");
            orderScheduler.shutdownNow();
            courierScheduler.shutdown();

            //6. Await for order/courier workers
            boolean isWait = false;
            do{
                isWait = courierScheduler.awaitTermination(6, TimeUnit.SECONDS);
            } while(!isWait);
            log.warn("All courier workers completed");
            log.info("Total orders received:{}", receivedCount);
            log.info("Total orders cooked:{}", cookedCount);
            log.info("Total orders placed on hot shelf:{}", hotPlacedCount);
            log.info("Total orders placed on cold shelf:{}", coldPlacedCount);
            log.info("Total orders placed on frozen shelf:{}", frozenPlacedCount);
            log.info("Total orders placed on overflow Shelf:{}", overFlowPlacedCount);
            log.info("Total orders removed from overflow Shelf:{}", overFlowRemovedCount);
            log.info("Total orders moved from overflow Shelf:{}", overFlowMovedCount);
            log.info("Total orders picked:{}", pickedCount);
            log.info("Total orders decayed:{}", decayedCount);
            log.info("Total orders delivered:{}", deliveredCount);

        } catch (InterruptedException e) {
            log.error("Interruption while waiting for order/courier workers. Now force shutdown");
            System.exit(1);
        }
    }
}
