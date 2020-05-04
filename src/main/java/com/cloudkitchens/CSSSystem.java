package com.cloudkitchens;

import com.cloudkitchens.dtos.Order;
import com.cloudkitchens.utils.OrderReader;
import com.cloudkitchens.workers.OrderWorker;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Starter Class of Kitchen Orders App.
 */
@Slf4j
public class CSSSystem {

    public static BlockingQueue<Order> ordersToBeProcessed = new LinkedBlockingQueue<>();
    public static boolean isOrdersProcessed;
    /**
     * Main method
     * @param args - Order Ingestion rate (Defined in no of orders per sec format)
     */
    public static void main(final String[] args) {
        int ingestionRate = 0;

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
            System.exit(0);
        }

        // 2. Read Orders from orders.json file
        ordersToBeProcessed.addAll(new OrderReader().readOrders());

        // 3. Send Orders to Kitchen as per Ingestion Rate
        ScheduledExecutorService orderScheduler =
            Executors.newScheduledThreadPool(ingestionRate);
        orderScheduler.scheduleAtFixedRate(new OrderWorker(),
                    0, 1000/ingestionRate, TimeUnit.MILLISECONDS);

        try{
            // 4. Wait for all orders to be processed
            synchronized (CSSSystem.class) {
                while(!isOrdersProcessed) {
                    CSSSystem.class.wait();
                }
            }
            //5. Shutdown order workers
            log.warn("All order workers completed. Attempting to shutdown");
            orderScheduler.shutdown();

            //6. Await for courier workers
            if (!orderScheduler.awaitTermination(7, TimeUnit.SECONDS)) {
                log.warn("Still waiting after 7s: calling System.exit(0)...");
                System.exit(0);
            }
        } catch (InterruptedException e) {
            log.error("Error shutting down");
            System.exit(1);
        }
    }
}
