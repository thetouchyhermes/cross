package com;
import java.util.concurrent.*;
import java.io.*;

/**
 * The App class is the entry point of the application. It initializes the configuration,
 * sets up the thread pool, and manages the processing of accounts and records.
 */
public class App {
    private static int NUM_THREADS = Runtime.getRuntime().availableProcessors();

    /**
     * The main method of the application. It sets up the configuration, initializes the
     * thread pool, and manages the processing of accounts and records.
     * @param args command line arguments (none)
     */
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        ConfigFile config = new ConfigReader().getConfigFile();
        // Create synchronized data structure for Account exchange
        BlockingQueue<Account> q = new LinkedBlockingQueue<>();
        // Create synchronized data structure for Reason counter
        ReasonCounter counter = new ReasonCounter();

        // Create new cached thread pool
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        // Create new producer
        executor.submit(new ReaderTask(q));

        for (int i = 1; i <= NUM_THREADS; i++) {
            // Create new consumer
            executor.submit(new WorkerTask(q, counter));
        }
        
        ThreadPoolExecutor pool = (ThreadPoolExecutor) executor;
        while (pool.getActiveCount() == NUM_THREADS) {
            try {
                Thread.sleep(3000);
                System.out.println("Processing...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Try and close thread pool
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                // Force shutdown if not terminated in 60 seconds
                executor.shutdownNow();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Thread pool did not terminate correctly");
                }
            }
        } catch (InterruptedException e) {
            // Force shutdown if interrupted
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("All threads ended");

        try (FileWriter writer = new FileWriter(config.getOutputFile())) {
            writer.write(counter.toString());
            System.out.println("Records stats available on " + config.getOutputFile());
        } catch (IOException e) {
            System.err.println(e.getClass() + ": " + e.getMessage());
        }

        long endTime = System.currentTimeMillis();
        System.out.print("Execution time: " + (endTime - startTime) + " ms\nEnd of App <3\n");
    }
    
}
